// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 布局导入（纯函数）：支持简版 YAML 与 INI 两种布局文本转统一键位。
 * 非法行跳过，绝不抛异常；输出键位上限 64，避免解析与渲染失控。
 */
object LayoutImport {

    data class KeyDef(val label: String, val value: String)

    const val MAX_KEYS = 64

    fun parseYaml(text: String): List<KeyDef> {
        val out = mutableListOf<KeyDef>()
        for (raw in text.lines()) {
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) continue
            // 形如：label: value
            val idx = line.indexOf(':')
            if (idx <= 0) continue
            val label = line.substring(0, idx).trim()
            val value = line.substring(idx + 1).trim().trim('"', '\'')
            if (label.isEmpty() || value.isEmpty()) continue
            out.add(KeyDef(label, value))
            if (out.size >= MAX_KEYS) break
        }
        return out
    }

    fun parseIni(text: String): List<KeyDef> {
        val out = mutableListOf<KeyDef>()
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
            val value = line.substring(idx + 1).trim()
            if (label.isEmpty() || value.isEmpty()) continue
            out.add(KeyDef(if (section != null) "$section:$label" else label, value))
            if (out.size >= MAX_KEYS) break
        }
        return out
    }
}
