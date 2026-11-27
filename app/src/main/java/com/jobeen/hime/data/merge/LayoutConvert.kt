// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 布局转换（纯函数）：统一键位在 YAML 与 INI 之间互转，
 * 以及主流与 Dvorak 键位映射。转换均产出新列表，
 * 输入上限 64 键，单键标签/值上限 64 字符；非法输入返回空，
 * 不抛异常。转换不加载视图，性能可忽略。
 */
object LayoutConvert {

    private val dvorak = mapOf(
        'a' to 'a', 'b' to 'x', 'c' to 'j', 'd' to 'e', 'e' to 'd', 'f' to 'u',
        'g' to 'i', 'h' to 'h', 'i' to 'g', 'j' to 'c', 'k' to 't', 'l' to 'k',
        'm' to 'm', 'n' to 'b', 'o' to 'r', 'p' to 'l', 'q' to '\'', 'r' to 'p',
        's' to 'o', 't' to 'y', 'u' to 'f', 'v' to '.', 'w' to ',', 'x' to 'q',
        'y' to 'n', 'z' to ';'
    )

    fun toYaml(keys: List<LayoutImport.KeyDef>): String =
        keys.take(LayoutImport.MAX_KEYS)
            .joinToString("\n") { "${it.label}: ${it.value}".take(128) }

    fun toIni(keys: List<LayoutImport.KeyDef>): String =
        keys.take(LayoutImport.MAX_KEYS)
            .joinToString("\n") { "${it.label}=${it.value}".take(128) }

    /** 按 Dvorak 映射替换单字符值，其余原样保留。 */
    fun toDvorak(keys: List<LayoutImport.KeyDef>): List<LayoutImport.KeyDef> =
        keys.take(LayoutImport.MAX_KEYS).map { k ->
            val v = k.value.lowercase()
            val mapped = if (v.length == 1) dvorak[v[0]]?.toString() ?: v else v
            LayoutImport.KeyDef(k.label, mapped)
        }
}
