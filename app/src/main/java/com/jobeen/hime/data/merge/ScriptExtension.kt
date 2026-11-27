// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 脚本扩展（纯函数）：清单校验与命令白名单。脚本只声明
 * 命令名与参数个数，不内嵌可执行代码；单脚本命令上限 16 个，
 * 参数名长度上限 32，清单总长上限 4096 字符。
 * 校验失败返回原因而不抛异常，恶意脚本无法借此提权。
 */
object ScriptExtension {

    const val MAX_COMMANDS = 16
    const val MAX_PARAM_LEN = 32

    data class Command(val name: String, val paramCount: Int)

    fun validate(name: String, version: String, commands: List<Command>): String? {
        if (name.isBlank()) return "name empty"
        if (version.isBlank()) return "version empty"
        if (commands.isEmpty()) return "commands empty"
        if (commands.size > MAX_COMMANDS) return "too many commands"
        for (c in commands) {
            if (c.name.isBlank()) return "command name empty"
            if (c.name.length > MAX_PARAM_LEN) return "command name too long"
            if (c.paramCount < 0 || c.paramCount > 8) return "bad paramCount"
        }
        return null
    }

    fun allows(commands: List<Command>, name: String): Boolean =
        commands.any { it.name == name }
}
