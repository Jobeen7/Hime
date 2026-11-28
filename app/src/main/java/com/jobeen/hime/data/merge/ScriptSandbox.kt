// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 脚本沙箱执行器（纯函数）：仅白名单命令、步数与参数上限、 deny-by-default。 */
object ScriptSandbox {
    const val MAX_STEPS = 64
    enum class Code { OK, DENIED, TOO_MANY, BAD_ARG }
    data class Outcome(val code: Code, val value: String = "", val stepsUsed: Int = 0)
    fun run(commands: List<ScriptExtension.Command>, script: List<String>): Outcome {
        if (script.size > MAX_STEPS) return Outcome(Code.TOO_MANY)
        var steps = 0
        for (name in script) {
            steps++
            if (!ScriptExtension.allows(commands, name.trim())) return Outcome(Code.DENIED, stepsUsed = steps)
            val cmd = commands.first { it.name == name.trim() }
            if (cmd.paramCount < 0 || cmd.paramCount > 8) return Outcome(Code.BAD_ARG, stepsUsed = steps)
        }
        return Outcome(Code.OK, value = "executed:${script.size}", stepsUsed = steps)
    }
    fun quota(script: List<String>): String = "steps=${minOf(script.size, MAX_STEPS)}/$MAX_STEPS"
}
