// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 布局导入（纯函数）：支持简版 YAML 与 INI 两种布局文本转统一键位。
 * 非法行跳过，绝不抛异常；输出键位上限 64，避免解析与渲染失控。
 */
object LayoutImport {

    data class KeyDef(val label: String, val value: String)

    const val MAX_KEYS = 64

    private fun stripQuotes(s: String): String {
        val v = s.trim()
        return if (v.length >= 2 && ((v[0] == '"' && v.last() == '"') || (v[0] == '\'' && v.last() == '\'')))
            v.substring(1, v.length - 1) else v
    }

    private fun stripInlineComment(s: String): String {
        // 引号外遇到 " #" 截断（YAML/INI 行内注释）
        var inSingle = false; var inDouble = false
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\'' && !inDouble) inSingle = !inSingle
            else if (c == '"' && !inSingle) inDouble = !inDouble
            else if (c == '#' && !inSingle && !inDouble && i > 0 && (s[i - 1] == ' ' || s[i - 1] == '\t'))
                return s.substring(0, i).trim()
            i++
        }
        return s.trim()
    }

    fun parseYaml(text: String): List<KeyDef> {
        val out = mutableListOf<KeyDef>()
        val seen = mutableSetOf<String>()
        for (raw in text.lines()) {
            var line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) continue
            if (line.startsWith("- ")) line = line.substring(2).trim() // 兼容列表项
            val idx = line.indexOf(':')
            if (idx <= 0) continue
            val label = line.substring(0, idx).trim()
            val value = stripQuotes(stripInlineComment(line.substring(idx + 1)))
            if (label.isEmpty() || value.isEmpty()) continue
            if (!seen.add(label)) continue // 重复标签保留首个，保持确定性
            out.add(KeyDef(label, value))
            if (out.size >= MAX_KEYS) break
        }
        return out
    }

    fun parseIni(text: String): List<KeyDef> {
        val out = mutableListOf<KeyDef>()
        val seen = mutableSetOf<String>()
        var section: String? = null
        for (raw in text.lines()) {
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) continue
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length - 1).trim()
                continue
            }
            val idx = line.indexOf('=')
            if (idx <= 0) continue
            val label = line.substring(0, idx).trim()
            val value = stripQuotes(stripInlineComment(line.substring(idx + 1)))
            if (label.isEmpty() || value.isEmpty()) continue
            val full = if (section != null) "$section:$label" else label
            if (!seen.add(full)) continue
            out.add(KeyDef(full, value))
            if (out.size >= MAX_KEYS) break
        }
        return out
    }
}
