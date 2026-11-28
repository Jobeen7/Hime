// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 可视化/网页布局编辑器的纯函数模型与成对符号、局域网服务守卫。
 * 键身份以 stableId 为准：重排只改顺序不改 ID，丢键直接拒绝。
 * 网络/文件 IO 全部在调用方，热路径不做 IO；实体键盘与虚拟键盘共用退格判定。
 */
object LayoutEditorModel {
    fun reorder(ids: List<String>, moves: List<Pair<Int, Int>>): List<String>? {
        val cur = ids.toMutableList()
        if (cur.toSet().size != cur.size) return null
        for (m in moves) {
            val from = m.first; val to = m.second
            if (from < 0 || from >= cur.size || to < 0 || to >= cur.size) return null
            val v = cur.removeAt(from)
            cur.add(to, v)
        }
        if (cur.toSet() != ids.toSet()) return null
        return cur
    }

    fun validateIds(ids: List<String>): Boolean {
        if (ids.isEmpty()) return false
        if (ids.toSet().size != ids.size) return false
        for (id in ids) if (id.trim().length == 0) return false
        return true
    }
}

object PairSymbol {
    private val PAIRS = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>', '"' to '"', '\'' to '\'', '“' to '”', '‘' to '’', '「' to '」', '《' to '》', '【' to '】')
    private val CLOSES = setOf(')', ']', '}', '>', '"', '\'', '”', '’', '」', '》', '】')

    fun closeFor(open: Char): Char? = PAIRS[open]

    /** 输入 open 后是否应自动补全 close（调用方已去重时才补）。 */
    fun shouldAutocomplete(open: Char, next: Char?): Boolean {
        if (next == null) return PAIRS.containsKey(open)
        return PAIRS.containsKey(open) && next != PAIRS[open]
    }

    /** 光标位于成对符号内部时退格是否应连同删除闭合符。 */
    fun shouldDeletePair(content: String, cursor: Int): Boolean {
        if (cursor < 1 || cursor >= content.length) return false
        val left = content[cursor - 1]
        val right = content[cursor]
        return PAIRS[left] == right
    }

    /** 输入 open 后的新文本（仅补全，不改其它区域）。 */
    fun applyPair(content: String, cursor: Int, open: Char): String? {
        val close = PAIRS[open] ?: return null
        if (cursor < 0 || cursor > content.length) return null
        return content.substring(0, cursor) + open.toString() + close.toString() + content.substring(cursor)
    }

    /** 中英混输自动插空格：英文单词后接汉字或汉字后接英文时插空格。 */
    fun needsAutoSpace(prev: Char?, next: Char?, enabled: Boolean): Boolean {
        if (!enabled || prev == null || next == null) return false
        fun isEn(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z') || (c in '0'..'9')
        fun isCn(c: Char) = c.toInt() in 0x4E00..0x9FFF
        return (isEn(prev) && isCn(next)) || (isCn(prev) && isEn(next))
    }
}

object WebEditorGuard {
    /** 一次性口令校验（PIN 等长至少 4 位数字）。 */
    fun checkPin(expected: String, given: String): Boolean {
        if (expected.length < 4 || given.length != expected.length) return false
        return expected == given
    }

    fun isValidPin(pin: String): Boolean {
        if (pin.length < 4 || pin.length > 8) return false
        for (c in pin) if (c !in '0'..'9') return false
        return true
    }

    /** 端口守卫：同一时刻只允许一个网页服务占用。 */
    class PortGuard {
        private var occupied = -1
        fun acquire(port: Int): Boolean {
            if (occupied != -1) return false
            if (port !in 1024..65535) return false
            occupied = port
            return true
        }
        fun release(): Boolean {
            if (occupied == -1) return false
            occupied = -1
            return true
        }
        fun isOccupied(): Boolean = occupied != -1
    }
}
