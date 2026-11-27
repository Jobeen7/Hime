// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 智能助手（纯函数）：提示词组装与回复清洗。提示词总长上限
 * 4000 字符，回复清洗后截断 8000 字符；仅做模板与截断，
 * 网络请求由调用方在开关开启后发起，本模块不联网、不读配置，
 * 在输入法进程里常驻零负担。
 */
object Assistant {

    const val MAX_PROMPT = 4000
    const val MAX_REPLY = 8000

    enum class Intent { SUMMARIZE, TRANSLATE, REWRITE, UNKNOWN }

    fun buildPrompt(intent: Intent, context: String): String {
        val ctx = context.trim().take(3000)
        val head = when (intent) {
            Intent.SUMMARIZE -> "请把下面文本summarize:"
            Intent.TRANSLATE -> "请把下面文本translate:"
            Intent.REWRITE -> "请把下面文本rewrite:"
            Intent.UNKNOWN -> "请处理:"
        }
        return (head + " " + ctx).take(MAX_PROMPT)
    }

    fun parseIntent(command: String): Intent = when (command.trim().lowercase()) {
        "/summarize", "总结" -> Intent.SUMMARIZE
        "/translate", "翻译" -> Intent.TRANSLATE
        "/rewrite", "改写" -> Intent.REWRITE
        else -> Intent.UNKNOWN
    }

    fun cleanReply(reply: String): String =
        reply.trim().lines().joinToString("\n") { it.trimEnd() }.take(MAX_REPLY)
}
