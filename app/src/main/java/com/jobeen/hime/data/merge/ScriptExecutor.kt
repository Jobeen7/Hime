// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 脚本执行器：仅白名单命令 log/echo/now/len/upper，参数来自 args 表，超步数即停。 */
object ScriptExecutor {
    private val allowed = setOf("log", "echo", "now", "len", "upper")
    fun exec(commands: List<ScriptExtension.Command>, script: List<String>, args: Map<String, String>): ScriptSandbox.Outcome {
        if (script.size > ScriptSandbox.MAX_STEPS) return ScriptSandbox.Outcome(ScriptSandbox.Code.TOO_MANY)
        val outputs = mutableListOf<String>()
        var steps = 0
        for (raw in script) {
            steps++
            val name = raw.trim()
            if (!ScriptExtension.allows(commands, name) || name !in allowed) {
                return ScriptSandbox.Outcome(ScriptSandbox.Code.DENIED, value = outputs.joinToString(" "), stepsUsed = steps)
            }
            val cmd = commands.first { it.name == name }
            if (cmd.paramCount < 0 || cmd.paramCount > 8) return ScriptSandbox.Outcome(ScriptSandbox.Code.BAD_ARG, stepsUsed = steps)
            val arg = (args[name] ?: "").trim()
            outputs.add(when (name) {
                "log", "echo" -> arg
                "now" -> "t"
                "len" -> arg.length.toString()
                "upper" -> arg.uppercase()
                else -> ""
            })
        }
        return ScriptSandbox.Outcome(ScriptSandbox.Code.OK, value = outputs.joinToString(" "), stepsUsed = steps)
    }
}
