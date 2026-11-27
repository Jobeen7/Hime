// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 统一备份（纯函数）：备份清单组装与完整性校验。条目上限 512，
 * 每个条目记录相对路径、字节数与校验和；加密由调用方在文件层
 * 完成，本模块只校验清单，不做 IO，不占大内存。
 */
object UnifiedBackup {

    const val MAX_ENTRIES = 512
    const val SCHEMA = 1

    data class Entry(val path: String, val bytes: Long, val checksum: String)

    data class Manifest(val schema: Int, val entries: List<Entry>)

    fun build(entries: List<Entry>): Manifest? {
        val clean = entries.filter { it.path.isNotBlank() && it.bytes >= 0 && it.checksum.length == 8 }
        if (clean.size != entries.size) return null
        if (clean.size > MAX_ENTRIES) return null
        return Manifest(SCHEMA, clean)
    }

    /** 校验清单：schema、路径、条目数全部合法才返回 true。 */
    fun verify(manifest: Manifest): Boolean {
        if (manifest.schema != SCHEMA) return false
        if (manifest.entries.size > MAX_ENTRIES) return false
        if (manifest.entries.isEmpty()) return false
        return manifest.entries.all {
            it.path.isNotBlank() && !it.path.startsWith("/") && it.bytes >= 0 && it.checksum.length == 8
        }
    }
}
