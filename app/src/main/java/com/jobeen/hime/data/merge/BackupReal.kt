// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
/** 备份文件 IO：原子写入的文件 Store；AES-GCM 真加密（javax.crypto）。 */
object BackupReal {
    class FileBackupStore(val dir: File) : BackupIo.Store {
        private fun safe(path: String): File? {
            if (path.isBlank() || path.contains("..") || path.contains("/") || path.contains("\\")) return null
            val f = File(dir, path)
            val base = dir.canonicalPath
            val target = f.canonicalFile.path
            return if (target.startsWith(base)) f else null
        }
        override fun write(path: String, bytes: ByteArray): Boolean {
            val f = safe(path) ?: return false
            if (bytes.size > BackupIo.MAX_BYTES) return false
            return try {
                dir.mkdirs()
                val tmp = File(dir, path + ".tmp")
                tmp.writeBytes(bytes)
                tmp.renameTo(f)
                f.exists() && f.readBytes().contentEquals(bytes)
            } catch (e: Exception) { false }
        }
        override fun read(path: String): ByteArray? {
            val f = safe(path) ?: return null
            return try { if (f.exists() && f.length() <= BackupIo.MAX_BYTES) f.readBytes() else null } catch (e: Exception) { null }
        }
        override fun list(): List<String> {
            val files = dir.listFiles() ?: return emptyList()
            return files.filter { it.isFile && !it.name.endsWith(".tmp") && !it.name.contains("..") }.map { it.name }.sorted()
        }
    }
    object Crypto {
        private val rng = SecureRandom()
        fun encrypt(key: ByteArray, plain: ByteArray): ByteArray? {
            if (!BackupIo.validCrypto(key.size, 12, 128) || plain.size > BackupIo.MAX_BYTES) return null
            return try {
                val iv = ByteArray(12).also { rng.nextBytes(it) }
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
                iv + cipher.doFinal(plain)
            } catch (e: Exception) { null }
        }
        fun decrypt(key: ByteArray, blob: ByteArray): ByteArray? {
            if (!BackupIo.validCrypto(key.size, 12, 128) || blob.size < 28) return null
            return try {
                val iv = blob.copyOfRange(0, 12)
                val ct = blob.copyOfRange(12, blob.size)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
                cipher.doFinal(ct)
            } catch (e: Exception) { null }
        }
    }
}
