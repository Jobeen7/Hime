// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.compat

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * F10 百度 .bds 直读：.bds 实为 ZIP 包。运行时直接解包，把切图/底图清单与键位/音效映射为中间规格。
 * 本层只清单+解析（不把位图塞 SkinImageStore；落盘由 Android 侧调用方完成）。
 */
object BaiduBdsReader {

    data class BdsListing(val entries: List<String>, val layout: CompatLayout?)

    /** 解包并列举条目；同时尝试用包内 .ini/.txt 的键位行生成简版 Layout 规格（标签行：每行一个键）。 */
    fun listEntries(bdsBytes: ByteArray): List<String> = readEntries(bdsBytes).map { it.first }

    fun parse(bdsBytes: ByteArray, fallbackId: String): BdsListing? {
        val entries = readEntries(bdsBytes)
        if (entries.isEmpty()) return null
        val names = entries.map { it.first }
        val keyTextEntry = entries.firstOrNull { it.first.endsWith(".txt", true) || it.first.endsWith(".ini", true) }
        val layout = if (keyTextEntry != null) {
            val text = String(keyTextEntry.second, Charsets.UTF_8)
            val keys = text.lines().map { it.trim() }.filter { it.isNotEmpty() }.take(60)
            if (keys.isEmpty()) null else CompatLayout(
                layoutId = fallbackId,
                name = fallbackId,
                rows = listOf(keys.map { CompatKey(label = it, text = it) }),
                style = CompatStyleSnapshot(backgroundPath = names.firstOrNull { it.endsWith(".png", true) || it.endsWith(".jpg", true) })
            )
        } else null
        return BdsListing(entries = names, layout = layout)
    }

    private fun readEntries(bytes: ByteArray): List<Pair<String, ByteArray>> {
        if (bytes.isEmpty() || bytes.size < 4) return emptyList()
        val out = mutableListOf<Pair<String, ByteArray>>()
        try {
            ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
                var entry: ZipEntry? = zip.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val buf = ByteArrayOutputStream()
                        zip.copyTo(buf)
                        out.add(entry.name to buf.toByteArray())
                        if (out.size > 200) break
                    }
                    entry = zip.nextEntry
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return out
    }
}
