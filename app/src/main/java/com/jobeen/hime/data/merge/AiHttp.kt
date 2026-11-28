// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** AI 联网客户端接口（纯函数）：请求校验、预算、重试与回复解析；真实网络由调用方承载。 */
object AiHttp {
    const val MAX_BODY = 4000
    const val TIMEOUT_MS = 8000L
    const val MAX_RETRY = 2
    data class Request(val endpoint: String, val prompt: String, val maxTokens: Int)
    data class Response(val ok: Boolean, val text: String, val code: Int)
    interface Client { fun send(r: Request): Response }
    fun validate(r: Request): String? {
        if (!r.endpoint.startsWith("https://")) return "https required"
        if (r.prompt.isBlank() || r.prompt.length > MAX_BODY) return "prompt invalid"
        if (r.maxTokens < 32 || r.maxTokens > 4096) return "tokens invalid"
        return null
    }
    fun withRetry(client: Client, r: Request): Response {
        var last: Response? = null
        repeat(MAX_RETRY + 1) { last = client.send(r); if (last!!.ok) return last!! }
        return last!!
    }
    fun budget(tokens: Int): Int = tokens.coerceIn(32, 4096)
    fun parseReply(raw: String): String = Assistant.cleanReply(raw)
}
