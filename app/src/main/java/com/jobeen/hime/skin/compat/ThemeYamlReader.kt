// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.compat

/**
 * F10 YAML 主题直读：解析 .yaml 主题配色、按键样式、九宫格/全键布局与背景图路径，
 * 映射为 CompatLayout（再经 CompatLayoutMapper.toLayoutModel 进 Hime 主干）。
 * 仅实现常用子集：两空格缩进的 key: value、列表行 "- label"、九宫格 keys 段。
 */
object ThemeYamlReader {

    fun parse(yamlText: String, fallbackId: String): CompatLayout? {
        if (yamlText.isBlank()) return null
        val lines = yamlText.lines().map { it.removeSuffix("\r") }
        var name: String? = null
        var backgroundPath: String? = null
        var accent: String? = null
        val rows = mutableListOf<List<CompatKey>>()
        var currentRow = mutableListOf<CompatKey>()
        var inKeys = false
        var inNineGrid = false

        fun flushRow() {
            if (currentRow.isNotEmpty()) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
            }
        }

        for (raw in lines) {
            val line = raw.trimEnd()
            if (line.isBlank() || line.trimStart().startsWith("#")) continue
            val indent = line.length - line.trimStart().length
            val content = line.trim()
            val lower = content.lowercase()
            when {
                lower.startsWith("name:") -> name = stripQuotes(content.substringAfter(":")).ifBlank { null }
                lower.startsWith("background") && lower.contains(":") -> {
                    val v = stripQuotes(content.substringAfter(":"))
                    if (v.isNotBlank()) backgroundPath = v
                }
                lower.startsWith("accent") && lower.contains(":") -> {
                    val v = stripQuotes(content.substringAfter(":"))
                    if (v.isNotBlank()) accent = v
                }
                lower == "keys:" || content == "keys:" -> { inKeys = true; inNineGrid = false }
                lower == "nine_grid:" || lower.startsWith("nine_grid:") -> { inKeys = false; inNineGrid = true }
                (inKeys || inNineGrid) && content.startsWith("-") -> {
                    val body = content.removePrefix("-").trim()
                    if (body.isBlank()) { flushRow(); continue }
                    val key = CompatKey(label = stripQuotes(body.substringBefore(":").trim()), text = stripQuotes(body.substringAfter(":", "").trim()))
                    currentRow.add(key)
                }
                indent == 0 && currentRow.isNotEmpty() && !content.startsWith("-") -> {
                    flushRow()
                    inKeys = false
                    inNineGrid = false
                }
            }
        }
        flushRow()
        if (rows.isEmpty()) return null
        return CompatLayout(layoutId = fallbackId, name = name ?: fallbackId, rows = rows, style = CompatStyleSnapshot(backgroundPath = backgroundPath, accentColor = accent))
    }

    private fun stripQuotes(s: String): String = s.trim().trim('"').trim('\'')
}
