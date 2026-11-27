// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 剪贴板长文本分块（纯函数）：按字符上限切分，避免超长文本撑爆 UI。
 */
object ClipboardChunker {

    const val DEFAULT_LIMIT = 2000

    fun chunk(text: String, limit: Int = DEFAULT_LIMIT): List<String> {
        if (text.isEmpty()) return emptyList()
        val lim = if (limit <= 0) DEFAULT_LIMIT else limit
        val out = mutableListOf<String>()
        var i = 0
        while (i < text.length) {
            val end = minOf(i + lim, text.length)
            out.add(text.substring(i, end))
            i = end
        }
        return out
    }
}
