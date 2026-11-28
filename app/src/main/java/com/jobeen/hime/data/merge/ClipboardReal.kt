// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.io.File
/** 剪贴板同步文件持久化：队列落盘、时间合并与时钟回拨守卫（纯函数+文件）。 */
object ClipboardReal {
    const val MAX_KEEP = 32
    data class Timed(val id: String, val text: String, val atMs: Long)
    fun mergeByTime(a: List<Timed>, b: List<Timed>): List<Timed> {
        val map = linkedMapOf<String, Timed>()
        for (t in a + b) {
            if (t.id.isBlank() || t.text.isBlank() || t.text.length > ClipboardSync.MAX_TEXT_LEN) continue
            val old = map[t.id]
            if (old == null || t.atMs >= old.atMs) map[t.id] = t
        }
        return map.values.sortedByDescending { it.atMs }.take(MAX_KEEP).sortedBy { it.atMs }
    }
    fun serialize(items: List<Timed>): String = items.take(MAX_KEEP).joinToString("\n") { "${it.atMs}|${it.id}|${it.text.replace("\n", " ")}" }
    fun parse(raw: String): List<Timed> = raw.lines().mapNotNull { line ->
        val p = line.split("|", limit = 3)
        if (p.size != 3) null else {
            val at = p[0].toLongOrNull() ?: return@mapNotNull null
            val id = p[1].trim()
            val text = p[2].trim()
            if (id.isBlank() || text.isBlank()) null else Timed(id, text.take(ClipboardSync.MAX_TEXT_LEN), at)
        }
    }.sortedBy { it.atMs }.take(MAX_KEEP)
    fun save(dir: File, items: List<Timed>): Boolean {
        return try { dir.mkdirs(); File(dir, "clip-queue.txt").writeText(serialize(items), Charsets.UTF_8); true } catch (e: Exception) { false }
    }
    fun load(dir: File): List<Timed> {
        val f = File(dir, "clip-queue.txt")
        return try { if (f.exists()) parse(f.readText(Charsets.UTF_8)) else emptyList() } catch (e: Exception) { emptyList() }
    }
}
