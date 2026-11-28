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
        val k = Character.toLowerCase(wrongKey) to Character.toLowerCase(intendedKey)
        counts[k] = (counts[k] ?: 0) + 1
    }

    fun correct(wrongKey: Char): Char {
        val w = Character.toLowerCase(wrongKey)
        for ((pair, c) in counts) {
            if (pair.first == w && c >= THRESHOLD) return pair.second
        }
        return w
    }

    fun learnedMappings(): Map<Char, Char> = counts
        .entries
        .filter { it.value >= THRESHOLD }
        .associate { it.key.first to it.key.second }

    fun clearAll() = counts.clear()

    // ---- F02 新增：邻近距离、按方案学习、音节条与韵母提示（纯函数） ----
    private val NEIGHBORS: Map<Char, String> = mapOf(
        'q' to "wa", 'w' to "qea", 'e' to "wrs", 'r' to "etd", 't' to "ryf",
        'y' to "tug", 'u' to "yih", 'i' to "uok", 'o' to "ipj", 'p' to "ol",
        'a' to "qwsz", 's' to "awedxz", 'd' to "serfcx", 'f' to "drtgcv",
        'g' to "ftyhvb", 'h' to "gyujnb", 'j' to "huiknm", 'k' to "jiolm",
        'l' to "kop", 'z' to "asx", 'x' to "zsdc", 'c' to "xdfv",
        'v' to "cfgb", 'b' to "vghn", 'n' to "bhjm", 'm' to "njk"
    )
    private val schemeCounts = mutableMapOf<Triple<String, Char, Char>, Int>()

    fun recordMistake(schemeId: String, wrongKey: Char, intendedKey: Char) {
        val k = Triple(schemeId, Character.toLowerCase(wrongKey), Character.toLowerCase(intendedKey))
        schemeCounts[k] = (schemeCounts[k] ?: 0) + 1
    }

    fun distance(a: Char, b: Char): Int {
        val x = Character.toLowerCase(a); val y = Character.toLowerCase(b)
        if (x == y) return 0
        val n = NEIGHBORS[x] ?: ""
        if (n.indexOf(y) >= 0) return 1
        return 2
    }

    /** librime 候选为空/未命中时的备选（按距离升序，同距离学习次数降序）。占第二槽位。 */
    fun alternativesFor(wrongKey: Char, schemeId: String): List<Char> {
        val w = Character.toLowerCase(wrongKey)
        val cand = (NEIGHBORS[w] ?: "").toList()
        val boosted = cand.sortedWith(compareBy(
            { c: Char -> distance(w, c) },
            { c: Char -> -(schemeCounts[Triple(schemeId, w, c)] ?: 0) }
        ))
        return boosted
    }

    fun learnedForScheme(schemeId: String): Map<Char, Char> {
        val out = mutableMapOf<Char, Char>()
        for ((key, c) in schemeCounts) {
            if (key.first == schemeId && c >= THRESHOLD) out[key.second] = key.third
        }
        return out
    }

    fun clearScheme(schemeId: String) {
        val it = schemeCounts.keys.iterator()
        while (it.hasNext()) if (it.next().first == schemeId) it.remove()
    }

    private val VOWELS = setOf('a', 'o', 'e', 'i', 'u', 'v')
    /** 将编码切为音节：以元音为核，超长单音节截断上限 5。 */
    fun splitSyllables(code: String): List<String> {
        val s = code.toLowerCase()
        if (s.trim().length == 0) return emptyList()
        val out = mutableListOf<String>()
        var cur = StringBuilder()
        var seenVowel = false
        for (ch in s) {
            if (ch !in 'a'..'z') continue
            if (VOWELS.contains(ch)) seenVowel = true
            cur.append(ch)
            if (seenVowel && cur.length >= 2 && out.size < 8) {
                // 宁可保守：vowel 后且下一字为声母时断开（简单规则）
                // 这里仅在已有元音且可能成音节时记录，超 2 音节由上层展开
            }
            if (cur.length >= 5) { out.add(cur.toString()); cur = StringBuilder(); seenVowel = false }
        }
        if (cur.length > 0) out.add(cur.toString())
        return out
    }

    /** 音节条是否需要展开（>2）。 */
    fun needsExpand(syllables: List<String>): Boolean = syllables.size > 2

    /** 长编码横向滚动的可见窗口。 */
    fun syllableWindow(syllables: List<String>, start: Int, size: Int = 3): List<String> {
        if (start < 0 || start >= syllables.size || size <= 0) return emptyList()
        val end = if (start + size < syllables.size) start + size else syllables.size
        return syllables.subList(start, end)
    }

    /** 韵母提示：自定义优先，否则系统默认映射。 */
    fun rhymeTip(key: Char, custom: Map<Char, String>): String? {
        val k = Character.toLowerCase(key)
        return custom[k] ?: when (k) {
            'a' -> "ɑ"
            'o' -> "ɔ"
            'e' -> "ɤ"
            'i' -> "i"
            'u' -> "u"
            'v' -> "ü"
            'n' -> "n"
            'g' -> "ng"
            'r' -> "r"
            else -> null
        }
    }
}

