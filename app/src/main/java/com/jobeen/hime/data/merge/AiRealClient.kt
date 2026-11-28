// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.net.HttpURLConnection
import java.net.URL
/** AI 联网客户端：HttpURLConnection 实现，带超时与 https 强制校验（真实网络代码）。 */
object AiRealClient {
    class UrlClient(var timeoutMs: Int = AiHttp.TIMEOUT_MS.toInt()) : AiHttp.Client {
        override fun send(r: AiHttp.Request): AiHttp.Response {
            val err = AiHttp.validate(r)
            if (err != null) return AiHttp.Response(false, err, 0)
            return try {
                val conn = (URL(r.endpoint) as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = timeoutMs
                    readTimeout = timeoutMs
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                }
                val body = "{\"prompt\":\"" + r.prompt.replace("\"", "'") + "\",\"maxTokens\":" + r.maxTokens + "}"
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = (stream ?: return AiHttp.Response(false, "", code)).bufferedReader(Charsets.UTF_8).use { it.readText() }
                AiHttp.Response(code in 200..299, AiHttp.parseReply(text), code)
            } catch (e: Exception) { AiHttp.Response(false, "", 0) }
        }
    }
    fun budgetMs(tokens: Int, timeoutMs: Long): Long = timeoutMs + tokens.toLong()
}
