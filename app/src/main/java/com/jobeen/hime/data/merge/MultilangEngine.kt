// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 多语言引擎路由（纯函数）：按字符类别把文本路由到对应引擎 ID，
 * 引擎实例由调用方懒加载，本表只存 ID 与语言映射。
 * 无文本/全空白返回默认引擎；总映射上限 32 项，避免无界增长。
 */
object MultilangEngine {

    const val DEFAULT_ENGINE = "pinyin"

    private val registry = linkedMapOf(
        "zh" to "pinyin",
        "en" to "latin",
        "ja" to "kana",
        "ko" to "hangul",
        "zhuyin" to "zhuyin",
        "jyutping" to "jyutping"
    )

    fun registered(): Map<String, String> = registry.toMap()

    fun register(lang: String, engine: String): Boolean {
        val l = lang.trim().lowercase()
        val e = engine.trim()
        if (l.isEmpty() || e.isEmpty() || registry.size >= 32) return false
        registry[l] = e
        return true
    }

    /** 按字符类别粗判语言并返回引擎 ID。 */
    fun route(text: String): String {
        if (text.isBlank()) return DEFAULT_ENGINE
        var cjk = 0; var latin = 0; var kana = 0; var hangul = 0
        for (ch in text) {
            when {
                ch in '\u3040'..'\u30FF' -> kana++
                ch in '\uAC00'..'\uD7AF' -> hangul++
                ch in '\u4E00'..'\u9FFF' -> cjk++
                ch.isLetter() -> latin++
            }
        }
        return when {
            hangul > 0 && hangul >= cjk -> registry.getValue("ko")
            kana > 0 -> registry.getValue("ja")
            cjk >= latin -> registry.getValue("zh")
            latin > 0 -> registry.getValue("en")
            else -> DEFAULT_ENGINE
        }
    }
}
