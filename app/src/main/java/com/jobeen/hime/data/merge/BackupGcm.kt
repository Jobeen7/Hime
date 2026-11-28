// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 备份 AES-GCM 真实加解密（JVM 可验证）：密钥 16/24/32 字节，
 * 随机 12 字节 nonce，128 位 tag；封装为 nonce + 密文（含 tag）。
 * 密钥/IV 长度不合规、密文过短、tag 校验失败返回 null，不抛异常上抛存储层。
 */
object BackupGcm {
    const val NONCE_LEN = 12
    const val TAG_BITS = 128
    const val MAX_PLAIN = 8 * 1024 * 1024

    private val secureRandom = SecureRandom()

    fun validKey(keyLen: Int): Boolean = keyLen == 16 || keyLen == 24 || keyLen == 32

    fun encrypt(key: ByteArray, plain: ByteArray): ByteArray? {
        if (!validKey(key.size) || plain.size > MAX_PLAIN) return null
        return try {
            val nonce = ByteArray(NONCE_LEN).also { secureRandom.nextBytes(it) }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(key, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(TAG_BITS, nonce))
            val cipherText = cipher.doFinal(plain)
            nonce + cipherText
        } catch (e: Exception) {
            null
        }
    }

    fun decrypt(key: ByteArray, payload: ByteArray): ByteArray? {
        if (!validKey(key.size) || payload.size < NONCE_LEN + (TAG_BITS / 8)) return null
        return try {
            val nonce = payload.copyOfRange(0, NONCE_LEN)
            val cipherText = payload.copyOfRange(NONCE_LEN, payload.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(key, "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(TAG_BITS, nonce))
            cipher.doFinal(cipherText)
        } catch (e: Exception) {
            null
        }
    }

    fun encryptText(key: ByteArray, text: String): String? {
        if (text.length > MAX_PLAIN / 4) return null
        val enc = encrypt(key, text.toByteArray(Charsets.UTF_8)) ?: return null
        return b64Encode(enc)
    }

    private val B64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    private fun b64Encode(data: ByteArray): String {
        val sb = StringBuilder((data.size + 2) / 3 * 4)
        var i = 0
        while (i < data.size) {
            val b0 = data[i].toInt() and 0xFF
            val b1 = if (i + 1 < data.size) data[i + 1].toInt() and 0xFF else 0
            val b2 = if (i + 2 < data.size) data[i + 2].toInt() and 0xFF else 0
            sb.append(B64[b0 ushr 2])
            sb.append(B64[((b0 and 0x03) shl 4) or (b1 ushr 4)])
            sb.append(if (i + 1 < data.size) B64[((b1 and 0x0F) shl 2) or (b2 ushr 6)] else '=')
            sb.append(if (i + 2 < data.size) B64[b2 and 0x3F] else '=')
            i += 3
        }
        return sb.toString()
    }

    private fun b64Decode(s: String): ByteArray? {
        val clean = s.trim()
        if (clean.isEmpty() || clean.length % 4 != 0) return null
        val out = ByteArray(clean.length / 4 * 3)
        var o = 0
        var i = 0
        while (i < clean.length) {
            val c0 = B64.indexOf(clean[i]); val c1 = B64.indexOf(clean[i + 1])
            val c2c = clean[i + 2]; val c3c = clean[i + 3]
            val c2 = if (c2c == '=') 0 else B64.indexOf(c2c)
            val c3 = if (c3c == '=') 0 else B64.indexOf(c3c)
            if (c0 < 0 || c1 < 0 || c2 < 0 || c3 < 0) return null
            if (o < out.size) out[o++] = ((c0 shl 2) or (c1 ushr 4)).toByte()
            if (c2c != '=' && o < out.size) out[o++] = (((c1 and 0x0F) shl 4) or (c2 ushr 2)).toByte()
            if (c3c != '=' && o < out.size) out[o++] = (((c2 and 0x03) shl 6) or c3).toByte()
            i += 4
        }
        val pad = clean.takeLast(2).count { it == '=' }
        return if (pad > 0) out.copyOf(out.size - pad) else out
    }

    fun decryptText(key: ByteArray, base64: String): String? {
        if (base64.isBlank()) return null
        val payload = b64Decode(base64) ?: return null
        val plain = decrypt(key, payload) ?: return null
        return plain.toString(Charsets.UTF_8)
    }
}
