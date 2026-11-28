// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** F01 候选字卡与词库导入（纯函数）：字卡组装、表情折叠开关、TXT/搜狗/QQ 解析与事务化导入（坏行整批回滚）。真实 Rime 用户词典写盘由调用方承载。 */
object CandidateDict {
    enum class Source { TXT, SOGOU, QQ }
    data class Detail(val text: String, val readings: List<String>, val radical: String = "", val strokes: Int = 0, val unicodeHex: String, val wubiCode: String = "")
    data class Entry(val word: String, val weight: Int)

    fun detailFor(text: String, readings: List<String> = emptyList(), radical: String = "", strokes: Int = 0, wubiCode: String = ""): Detail? {
        val t = text.trim()
        if (t.isBlank() || t.length > 10) return null
        val hex = t.map { "U+" + it.code.toString(16).uppercase().padStart(4, '0') }.joinToString(" ")
        return Detail(t, readings, radical, strokes.coerceIn(0, 100), hex, wubiCode.trim())
    }
    fun emojiCollapsed(collapse: Boolean): Boolean = collapse

    /** 解析词库文本：每行 "词 权重"（QQ 允许 "词" 无权重默认 1）；任一行非法整批返回 null，不做半导入。 */
    fun parse(text: String, source: Source): List<Entry>? {
        val out = mutableListOf<Entry>()
        for (raw in text.lines()) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            if (line.startsWith("#")) continue
            val parts = line.split(Regex("\\s+"))
            val word = parts[0].trim()
            if (word.isBlank() || word.length > 20) return null
            val weight = when {
                parts.size == 1 && source == Source.QQ -> 1
                parts.size == 2 -> parts[1].toIntOrNull() ?: return null
                else -> return null
            }
            if (weight < 0 || weight > 1_000_000) return null
            out.add(Entry(word, weight))
            if (out.size > 5000) return null
        }
        return out
    }
    /** 事务化合并：按词去重、权重取最大，保持首次出现顺序。 */
    fun importTransactional(existing: List<Entry>, incoming: List<Entry>): List<Entry> {
        val map = linkedMapOf<String, Entry>()
        for (e in existing) map[e.word] = e
        for (e in incoming) {
            val prev = map[e.word]
            if (prev == null || e.weight > prev.weight) map[e.word] = e
        }
        return map.values.toList()
    }
}
