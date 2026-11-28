// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** OCR 分页合并（纯函数）：按页合并段落、去重、总长上限。 */
object OcrPage {
    fun merge(pages: List<List<String>>, maxPages: Int = 20): List<String> {
        val seen = linkedSetOf<String>()
        val out = mutableListOf<String>()
        for (page in pages.take(maxPages)) for (seg in page) {
            val t = seg.trim()
            if (t.isNotEmpty() && seen.add(t)) out.add(t)
        }
        return out
    }
    fun pageText(segments: List<OcrResult.Segment>): String =
        OcrResult.filter(segments).joinToString("\n")
}
