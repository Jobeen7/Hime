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
        return java.util.Base64.getEncoder().encodeToString(enc)
    }

    fun decryptText(key: ByteArray, base64: String): String? {
        if (base64.isBlank()) return null
        val payload = try {
            java.util.Base64.getDecoder().decode(base64)
        } catch (e: Exception) {
            return null
        }
        val plain = decrypt(key, payload) ?: return null
        return plain.toString(Charsets.UTF_8)
    }
}
