// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 剪贴板真实连接过滤（纯函数）：去重、空白/超长过滤、映射为同步条目；不访问系统剪贴板。 */
object ClipboardLink {
    fun shouldIngest(text: String, lastText: String?): Boolean {
        val t = text.trim()
        return t.isNotEmpty() && text.length <= ClipboardSync.MAX_TEXT_LEN && text != lastText
    }
    fun toEntry(id: String, text: String, version: Int): ClipboardSync.Entry? {
        if (id.isBlank() || !shouldIngest(text, null)) return null
        return ClipboardSync.Entry(id.trim(), text.take(ClipboardSync.MAX_TEXT_LEN), version)
    }
    fun dedup(texts: List<String>): List<String> {
        val out = mutableListOf<String>(); var last: String? = null
        for (t in texts) if (shouldIngest(t, last)) { out.add(t); last = t }
        return out
    }
}
