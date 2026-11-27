// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 双拼邻近键纠错（纯函数）：统计“按错邻键又纠回目标键”的次数，
 * 达到阈值后把邻键映射到目标键。不入热路径，学习只在设置里触发。
 */
object NeighborCorrection {

    private const val THRESHOLD = 3
    private val counts = mutableMapOf<Pair<Char, Char>, Int>()

    fun recordMistake(wrongKey: Char, intendedKey: Char) {
        val k = wrongKey.lowercaseChar() to intendedKey.lowercaseChar()
        counts[k] = (counts[k] ?: 0) + 1
    }

    fun correct(wrongKey: Char): Char {
        val w = wrongKey.lowercaseChar()
        for ((pair, c) in counts) {
            if (pair.first == w && c >= THRESHOLD) return pair.second
        }
        return w
    }

    fun learnedMappings(): Map<Char, Char> = counts
        .filter { it.value >= THRESHOLD }
        .mapKeys { it.key.first }

    fun clearAll() = counts.clear()
}
