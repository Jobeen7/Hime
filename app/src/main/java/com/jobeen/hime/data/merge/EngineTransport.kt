// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 外部引擎传输层（纯函数）：心跳、退避、重连预算与健康。 */
object EngineTransport {
    const val HEARTBEAT_MS = 5000L
    const val MAX_BACKOFF_MS = 15000L
    data class Health(val ready: Boolean, val attempts: Int, val lastBeatMs: Long)
    fun backoff(attempt: Int): Long = (ExternalEngine.CONNECT_TIMEOUT_MS * (attempt + 1)).coerceIn(0, MAX_BACKOFF_MS)
    fun canReconnect(h: Health): Boolean = h.attempts < ExternalEngine.MAX_RETRY
    fun nextAttempt(h: Health): Health = h.copy(attempts = h.attempts + 1)
    fun beat(h: Health, now: Long): Health = h.copy(lastBeatMs = now)
    fun isAlive(h: Health, now: Long): Boolean = h.ready && (now - h.lastBeatMs) <= HEARTBEAT_MS * 2
    fun dialDescriptor(host: String, port: Int): String? {
        if (host.isBlank() || port !in 1024..65535) return null
        if (host.contains(" ") || host.contains("\n")) return null
        return "engine:$host:$port"
    }
}
