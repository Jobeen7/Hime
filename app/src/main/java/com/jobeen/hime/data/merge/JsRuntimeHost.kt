// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** F16 原生 JS 工具栏扩展：JsRuntimeHost 权限/域名白名单、超时、启停与撤销栈（纯函数，真实 Rhino 调用由宿主承载）。 */
object JsRuntimeHost {
    const val MIN_TIMEOUT_MS = 100
    const val MAX_TIMEOUT_MS = 5000
    data class Manifest(val name: String, val version: String, val allowedDomains: List<String>, val timeoutMs: Int, val enabled: Boolean = true)
    enum class Code { OK, DENIED, BAD_MANIFEST, TOO_MANY, TIMEOUT }
    data class Outcome(val code: Code, val value: String = "")

    private val domainPattern = Regex("^[a-z0-9.-]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

    fun validate(m: Manifest): String? {
        if (m.name.isBlank() || m.name.length > 64) return "name"
        if (m.version.isBlank() || m.version.length > 32) return "version"
        if (m.timeoutMs !in MIN_TIMEOUT_MS..MAX_TIMEOUT_MS) return "timeout"
        for (d in m.allowedDomains) {
            val t = d.trim()
            if (t.isEmpty() || t.startsWith("*")) return "domain wildcard denied"
            if (!domainPattern.matches(t)) return "domain format"
        }
        return null
    }
    fun canRequest(m: Manifest, host: String): Boolean {
        if (!m.enabled) return false
        val h = host.trim().lowercase()
        return m.allowedDomains.any { it.trim().lowercase() == h }
    }
    fun runWithBudget(steps: Int, budget: Int): Outcome {
        if (steps < 0 || budget <= 0) return Outcome(Code.BAD_MANIFEST)
        if (steps > budget) return Outcome(Code.TOO_MANY)
        return Outcome(Code.OK, "steps=$steps")
    }
    class UndoStack {
        private val stack = ArrayDeque<String>()
        fun apply(original: String, replacement: String): String {
            stack.addLast(original)
            if (stack.size > 20) stack.removeFirst()
            return replacement
        }
        fun undo(current: String): String? = if (stack.isEmpty()) null else stack.removeLast()
        fun size(): Int = stack.size
    }
}
