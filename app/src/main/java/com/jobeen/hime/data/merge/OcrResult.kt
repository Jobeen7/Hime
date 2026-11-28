// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 图像文字识别（纯函数）：构建识别任务参数，解析识别文本
 * （分行、去空、截断）。文本长度上限 4000 字符，行数上限 200；
 * 置信度低于阈值的片段丢弃。重型识别模型由调用方在开关开启后
 * 懒加载，本模块永不初始化模型，不占热路径内存。
 */
object OcrResult {

    const val MAX_CHARS = 4000
    const val MAX_LINES = 200
    const val MIN_CONFIDENCE = 0.5f

    data class Job(val imageId: String, val page: Int = 1, val maxChars: Int = MAX_CHARS)

    data class Segment(val text: String, val confidence: Float)

    fun buildJob(imageId: String, page: Int = 1): Job? {
        if (imageId.isBlank()) return null
        return Job(imageId.trim(), page.coerceIn(1, 99))
    }

    /** 解析原始 OCR 文本为干净段落，非法输入返回空列表；全长累计不超过 MAX_CHARS。 */
    fun parse(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        val lines = raw.lines().map { it.trim() }.filter { it.isNotEmpty() }.take(MAX_LINES)
        var total = 0
        return lines.mapNotNull { line ->
            if (total >= MAX_CHARS) return@mapNotNull null
            val room = (MAX_CHARS - total).coerceAtLeast(0)
            val seg = if (line.length > room) line.take(room) else line
            total += seg.length
            seg
        }
    }

    /** 按置信度过滤候选段，低于阈值丢弃，输出上限 64 段。 */
    fun filter(segments: List<Segment>): List<String> {
        return segments.filter { it.confidence >= MIN_CONFIDENCE && it.text.isNotBlank() }
            .take(64).map { it.text.trim() }
    }
}
