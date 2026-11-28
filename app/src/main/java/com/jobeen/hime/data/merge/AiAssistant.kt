// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** F12 AI 文本助手与翻译（纯函数）：多服务商请求校验、预览-确认与焦点拦截、缓存隔离。真实网络由调用方承载，本模块不联网、不读密钥。 */
object AiAssistant {
    const val MAX_TEXT = 4000
    enum class Provider { MICROSOFT, TENCENT, VOLC, BILIBILI, YOUDAO, AI_GENERIC }
    enum class Op { REWRITE, TRANSLATE, QNA }
    data class Request(val provider: Provider, val op: Op, val lang: String, val text: String, val focusToken: Long)
    data class Preview(val text: String, val focusToken: Long)

    fun buildRequest(provider: Provider, op: Op, lang: String, text: String, focusToken: Long): Request? {
        val t = text.trim()
        if (t.isBlank() || t.length > MAX_TEXT) return null
        if (op == Op.TRANSLATE && lang.trim().isBlank()) return null
        return Request(provider, op, lang.trim().lowercase(), t, focusToken)
    }
    fun makePreview(result: String, focusToken: Long): Preview? {
        val t = result.trim()
        if (t.isBlank()) return null
        return Preview(t, focusToken)
    }
    /** 提交预览：焦点 token 不一致则拦截，防止旧结果脏写。 */
    fun commit(preview: Preview, currentFocusToken: Long): String? =
        if (preview.focusToken != currentFocusToken) null else preview.text
    fun cacheKey(provider: Provider, op: Op, lang: String, text: String): String =
        provider.name + ":" + op.name + ":" + lang.trim().lowercase() + ":" + text.trim()
    fun allowedProviders(): List<Provider> = Provider.values().toList()
    interface StreamClient { fun lines(req: Request): List<String> }
    /** 流式断线仿真：遇到以 DISCONNECT 开头的行即停止，返回已收行；取消不抛异常、不脏写。 */
    fun stream(client: StreamClient, req: Request): List<String> {
        val out = mutableListOf<String>()
        for (l in client.lines(req)) {
            if (l.startsWith("DISCONNECT")) break
            out.add(l)
            if (out.size >= 100) break
        }
        return out
    }
}
