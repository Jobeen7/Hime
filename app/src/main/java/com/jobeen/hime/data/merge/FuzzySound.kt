// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 模糊音归一化（纯函数）：把常见模糊音拼写统一为标准拼写，
 * 便于词库命中与学习；未命中规则的音节原样返回。
 */
object FuzzySound {

    // 规范化目标固定（单向，避免循环）：s/z 归 z，f/h 归 h，p/b 归 b，t/d 归 d，k/g 归 g
    private val rules = mapOf(
        "zh" to "z",
        "ch" to "c",
        "sh" to "s",
        "s" to "z",
        "l" to "n",
        "r" to "l",
        "f" to "h",
        "k" to "g",
        "p" to "b",
        "t" to "d"
    )

    fun normalize(pinyin: String): String {
        val p = pinyin.lowercase().trim()
        if (p.isEmpty()) return p
        // 前缀替换（最长匹配优先）
        val sorted = rules.entries.sortedByDescending { it.key.length }
        for ((k, v) in sorted) {
            if (p.startsWith(k) && p.length > k.length) {
                return v + p.substring(k.length)
            }
        }
        return p
    }

    fun isFuzzy(pinyin: String): Boolean = normalize(pinyin) != pinyin.lowercase()
}
