// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.input.calculator

/**
 * F15 候选栏计算器：无编码或特定算式输入时计算四则表达式。
 * 只支持 + - * /、括号、一元正负号与小数；不支持函数、幂、百分号。
 * 纯函数：不读输入法状态、不做 UI 注入。调用方在拿到非 null 结果后
 * 把结果写入首屏/扩展候选；null 表示不是合法算式或除零/非法，调用方按普通输入处理。
 * 除零返回 null（不抛、不返回 Infinity），避免误把除零当作合法候选。
 */
object CalculatorExpression {
    private const val MAX_INPUT_LEN = 64

    fun isLikelyExpression(input: String): Boolean {
        val s = input.trim().trimEnd('=')
        if (s.isEmpty() || s.length > MAX_INPUT_LEN) return false
        if (s.none { it.isDigit() }) return false
        return s.all { it.isDigit() || it == '.' || it == '+' || it == '-' || it == '*' || it == '/' || it == '(' || it == ')' || it.isWhitespace() }
    }

    /** 计算 [input]（可带末尾 '='）；非法或除零返回 null。 */
    fun evaluate(input: String): Double? {
        val s = input.trim().trimEnd('=').trim()
        if (!isLikelyExpression(s)) return null
        return try {
            val v = Parser(s.replace(" ", "")).parse()
            if (v.isFinite()) v else null
        } catch (e: Exception) {
            null
        }
    }

    /** 调用方直接可用的候选文本：格式化后的结果，失败返回 null。 */
    fun resolveCandidate(input: String): String? {
        val v = evaluate(input) ?: return null
        // 整数去尾零，避免 46.000000；保留至多 6 位小数，半数上舍入由 toString 保证可读。
        val s = if (v == v.toLong().toDouble() && v in -9e15..9e15) v.toLong().toString() else v.toString().trimEnd('0').trimEnd('.')
        return s.ifEmpty { null }
    }

    private class Parser(val s: String) {
        private var pos = 0
        fun parse(): Double {
            val v = parseExpr()
            if (pos != s.length) throw IllegalArgumentException("trailing")
            return v
        }
        private fun parseExpr(): Double {
            var v = parseTerm()
            while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
                val op = s[pos++]
                val rhs = parseTerm()
                v = if (op == '+') v + rhs else v - rhs
            }
            return v
        }
        private fun parseTerm(): Double {
            var v = parseFactor()
            while (pos < s.length && (s[pos] == '*' || s[pos] == '/')) {
                val op = s[pos++]
                val rhs = parseFactor()
                if (op == '*') v *= rhs else {
                    if (rhs == 0.0) throw IllegalArgumentException("div0")
                    v /= rhs
                }
            }
            return v
        }
        private fun parseFactor(): Double {
            if (pos >= s.length) throw IllegalArgumentException("eof")
            if (s[pos] == '-') { pos++; return -parseFactor() }
            if (s[pos] == '+') { pos++; return parseFactor() }
            if (s[pos] == '(') {
                pos++
                val v = parseExpr()
                if (pos >= s.length || s[pos] != ')') throw IllegalArgumentException("paren")
                pos++
                return v
            }
            val start = pos
            var dot = false
            while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) {
                if (s[pos] == '.') {
                    if (dot) throw IllegalArgumentException("dot")
                    dot = true
                }
                pos++
            }
            if (start == pos) throw IllegalArgumentException("number")
            return s.substring(start, pos).toDouble()
        }
    }
}
