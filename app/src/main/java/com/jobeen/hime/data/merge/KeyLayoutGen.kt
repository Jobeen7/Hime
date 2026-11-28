// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 自动 9/14/18/26 键生成与 18 键分组校验（纯函数，JVM 可验证）。
 * 松烟参考：读激活方案字母表自动生成兼容键位，切换布局不换词库。
 * 本模块只产出 LayoutModel 所需的键分组与排列数据，不另建渲染引擎。
 */
object KeyLayoutGen {
    val SUPPORTED_COUNTS = listOf(9, 14, 18, 26)

    private fun lower(c: Char): Char = Character.toLowerCase(c)

    /** 将 alphabet 切分为 keyCount 组（每字母恰出现一次）。非法计数返回空。 */
    fun generate(keyCount: Int, alphabet: String): List<String> {
        if (keyCount !in SUPPORTED_COUNTS) return emptyList()
        val lowerAlpha = alphabet.toLowerCase()
        val letters = StringBuilder()
        for (ch in lowerAlpha) {
            if (ch in 'a'..'z' && letters.indexOf(ch.toString()) < 0) letters.append(ch)
        }
        if (letters.length == 0) return emptyList()
        if (keyCount == 26) {
            if (letters.length != 26) return emptyList()
            val out = mutableListOf<String>()
            for (ch in letters) out.add(ch.toString())
            return out
        }
        if (letters.length < keyCount) return emptyList()
        val groups = mutableListOf<String>()
        var idx = 0
        val base = letters.length / keyCount
        val rem = letters.length % keyCount
        for (g in 0 until keyCount) {
            val size = base + if (g < rem) 1 else 0
            groups.add(letters.substring(idx, idx + size))
            idx += size
        }
        return groups
    }

    /** 分组是否精确覆盖 alphabet（每字母一次、无多余）。 */
    fun isValidPartition(groups: List<String>, alphabet: String): Boolean {
        for (g in groups) {
            if (g.length == 0) return false
            for (ch in g) if (ch !in 'a'..'z') return false
        }
        val joined = groups.joinToString("").toLowerCase()
        val seen = mutableSetOf<Char>()
        for (ch in joined) {
            if (seen.contains(ch)) return false
            seen.add(ch)
        }
        val expected = mutableSetOf<Char>()
        for (ch in alphabet.toLowerCase()) expected.add(ch)
        return seen == expected && joined.length == expected.size
    }

    /** 拆分第 index 组：在 at 处切成两组（at 须在 1..len-1）。失败返回 null。 */
    fun splitGroup(groups: List<String>, index: Int, at: Int): List<String>? {
        if (index < 0 || index >= groups.size) return null
        val g = groups[index]
        if (at < 1 || at >= g.length) return null
        val out = groups.toMutableList()
        out.removeAt(index)
        out.add(index, g.substring(at))
        out.add(index, g.substring(0, at))
        return out
    }

    /** 合并第 i、j 两组。失败返回 null。 */
    fun mergeGroups(groups: List<String>, i: Int, j: Int): List<String>? {
        if (i < 0 || i >= groups.size || j < 0 || j >= groups.size || i == j) return null
        val lo = if (i < j) i else j
        val hi = if (i < j) j else i
        val out = groups.toMutableList()
        out[lo] = out[lo] + out[hi]
        out.removeAt(hi)
        return out
    }

    private val COLEMAK = mapOf(
        'e' to 'f', 'r' to 's', 't' to 't', 'y' to 'g', 'u' to 'j', 'i' to 'l', 'o' to 'u', 'p' to ';',
        's' to 'r', 'd' to 's', 'f' to 't', 'g' to 'd', 'h' to 'h', 'j' to 'n', 'k' to 'e', 'l' to 'i',
        'a' to 'a', 'q' to 'q', 'w' to 'w', 'z' to 'z', 'x' to 'x', 'c' to 'c', 'v' to 'v', 'b' to 'b', 'n' to 'k', 'm' to 'm'
    )

    /** Colemak 映射（QWERTY 键 -> Colemak 键）。未映射回原键。 */
    fun colemak(key: Char): Char {
        val v = COLEMAK[lower(key)]
        return v ?: lower(key)
    }

    /** 蜂窝六边形排列：6 行数据（标签仅作几何占位，不定义渲染）。 */
    fun hexRows(): List<String> = listOf("123", "456", "789", "0qp", "wor", "tyu")
}
