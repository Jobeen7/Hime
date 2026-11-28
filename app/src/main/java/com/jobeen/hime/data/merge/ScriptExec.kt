// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 脚本执行器（真实执行）：在 ScriptExtension 白名单内执行文本命令。
 * 指令格式 name[:arg]，支持 load/append/upper/lower/replace/concat/
 * len/slice/clear；步数与文本长度均设上限，deny-by-default，
 * 非法指令返回 Denied/Error，不抛异常，不访问文件/网络。
 */
object ScriptExec {
    const val MAX_STEPS = 64
    const val MAX_TEXT = 4000

    enum class Code { OK, DENIED, TOO_MANY, BAD, TOO_LONG }

    data class Outcome(val code: Code, val value: String = "", val stepsUsed: Int = 0)

    fun run(commands: List<ScriptExtension.Command>, input: String, program: List<String>): Outcome {
        if (program.size > MAX_STEPS) return Outcome(Code.TOO_MANY)
        var text = if (input.length > MAX_TEXT) input.take(MAX_TEXT) else input
        var steps = 0
        for (raw in program) {
            steps++
            val line = raw.trim()
            if (line.isEmpty()) continue
            val idx = line.indexOf(':')
            val name = (if (idx >= 0) line.substring(0, idx) else line).trim()
            val arg = if (idx >= 0) line.substring(idx + 1) else ""
            if (!ScriptExtension.allows(commands, name)) return Outcome(Code.DENIED, stepsUsed = steps)
            val cmd = commands.first { it.name == name }
            val argc = if (idx >= 0) arg.split('|').size else 0
            if (argc != cmd.paramCount && !(cmd.paramCount == 0 && idx < 0)) {
                return Outcome(Code.BAD, stepsUsed = steps)
            }
            when (name) {
                "load" -> text = if (arg.length > MAX_TEXT) arg.take(MAX_TEXT) else arg
                "append" -> {
                    if ((text.length + arg.length) > MAX_TEXT) return Outcome(Code.TOO_LONG, stepsUsed = steps)
                    text += arg
                }
                "upper" -> text = text.uppercase()
                "lower" -> text = text.lowercase()
                "replace" -> {
                    val parts = arg.split('|')
                    if (parts.size != 2) return Outcome(Code.BAD, stepsUsed = steps)
                    text = text.replace(parts[0], parts[1])
                    if (text.length > MAX_TEXT) text = text.take(MAX_TEXT)
                }
                "concat" -> {
                    if ((text.length + arg.length) > MAX_TEXT) return Outcome(Code.TOO_LONG, stepsUsed = steps)
                    text = text + arg
                }
                "len" -> text = text.length.toString()
                "slice" -> {
                    val parts = arg.split('|')
                    if (parts.size != 2) return Outcome(Code.BAD, stepsUsed = steps)
                    val a = parts[0].toIntOrNull() ?: return Outcome(Code.BAD, stepsUsed = steps)
                    val b = parts[1].toIntOrNull() ?: return Outcome(Code.BAD, stepsUsed = steps)
                    val from = a.coerceIn(0, text.length)
                    val to = b.coerceIn(from, text.length)
                    text = text.substring(from, to)
                }
                "clear" -> text = ""
                else -> return Outcome(Code.DENIED, stepsUsed = steps)
            }
            if (text.length > MAX_TEXT) text = text.take(MAX_TEXT)
        }
        return Outcome(Code.OK, value = text, stepsUsed = steps)
    }
}
