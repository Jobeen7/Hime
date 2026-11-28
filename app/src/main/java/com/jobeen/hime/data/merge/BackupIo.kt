// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 备份文件 IO 与加密（纯抽象）：清单、摘要、AES-GCM 参数校验；Android 文件读写由调用方承载。 */
object BackupIo {
    const val MAX_BYTES = 8 * 1024 * 1024
    data class Blob(val path: String, val bytes: ByteArray)
    interface Store { fun write(path: String, bytes: ByteArray): Boolean; fun read(path: String): ByteArray?; fun list(): List<String> }
    class MemoryStore : Store {
        private val m = linkedMapOf<String, ByteArray>()
        override fun write(path: String, bytes: ByteArray): Boolean { if (path.isBlank() || path.contains("..") || bytes.size > MAX_BYTES) return false; m[path] = bytes.copyOf(); return true }
        override fun read(path: String): ByteArray? = m[path]?.copyOf()
        override fun list(): List<String> = m.keys.toList()
    }
    fun fnv1a(bytes: ByteArray): String { var h = 2166136261u; for (b in bytes) { h = h xor (b.toInt() and 0xFF).toUInt(); h *= 16777619u }; return "%08x".format(h.toInt()) }
    fun pack(manifest: UnifiedBackup.Manifest, store: Store): Boolean {
        if (!UnifiedBackup.verify(manifest)) return false
        val payload = manifest.entries.joinToString("\n") { "${it.path}|${it.bytes}|${it.checksum}" }.toByteArray(Charsets.UTF_8)
        return store.write("manifest.txt", payload) && store.write("manifest.digest", fnv1a(payload).toByteArray(Charsets.UTF_8))
    }
    fun unpack(store: Store): UnifiedBackup.Manifest? {
        val payload = store.read("manifest.txt") ?: return null
        val digest = store.read("manifest.digest")?.toString(Charsets.UTF_8) ?: return null
        if (fnv1a(payload) != digest) return null
        val entries = payload.toString(Charsets.UTF_8).lines().filter { it.isNotBlank() }.mapNotNull { line -> val p = line.split("|"); if (p.size != 3) null else UnifiedBackup.Entry(p[0], p[1].toLongOrNull() ?: -1, p[2]) }
        return UnifiedBackup.build(entries)
    }
    /** 加密参数校验：AES-GCM 需 12 字节 IV 与 128 位 tag，密钥长度 16/24/32。 */
    fun validCrypto(keyLen: Int, ivLen: Int, tagBits: Int): Boolean = (keyLen == 16 || keyLen == 24 || keyLen == 32) && ivLen == 12 && tagBits == 128
}
