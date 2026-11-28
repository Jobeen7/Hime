// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 剪贴板监听与同步传输（纯函数）：去抖、指纹、队列与冲突合并。 */
object ClipboardTransport {
    const val DEBOUNCE_MS = 400L
    const val MAX_QUEUE = 32
    data class Clip(val id: String, val text: String, val atMs: Long)
    data class Decision(val accepted: Boolean, val reason: String)
    fun fingerprint(text: String): String { var h = 2166136261u; for (b in text.toByteArray(Charsets.UTF_8)) { h = h xor (b.toInt() and 0xFF).toUInt(); h *= 16777619u }; return "%08x".format(h.toInt()) }
    fun shouldAccept(now: Long, lastAt: Long, text: String, lastText: String): Decision {
        if (text.isBlank()) return Decision(false, "blank")
        if (text.length > ClipboardSync.MAX_TEXT_LEN) return Decision(false, "too_long")
        if (text == lastText) return Decision(false, "duplicate")
        if (now - lastAt < DEBOUNCE_MS) return Decision(false, "debounce")
        return Decision(true, "ok")
    }
    fun enqueue(queue: List<Clip>, clip: Clip): List<Clip> {
        val dedup = queue.filter { it.id != clip.id } + clip
        return if (dedup.size > MAX_QUEUE) dedup.takeLast(MAX_QUEUE) else dedup
    }
    fun merged(local: List<ClipboardSync.Entry>, remote: List<ClipboardSync.Entry>): ClipboardSync.Diff = ClipboardSync.diff(local, remote)
}
