// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 外部引擎会话状态机（纯函数）：握手校验、重试退避、消息序号。
 * Socket/进程由 Android 侧承载，本模块只管状态，不在输入法热路径建连。
 */
object ExternalSession {
    enum class Phase { IDLE, HANDSHAKE, READY, FAILED }
    data class Session(val phase: Phase = Phase.IDLE, val attempts: Int = 0, val seq: Int = 0, val version: Int = 0)
    fun start(s: Session): Session = s.copy(phase = Phase.HANDSHAKE, attempts = s.attempts + 1, seq = 0)
    fun handshakeOk(s: Session, hs: ExternalEngine.Handshake): Session =
        if (hs.version > 0 && hs.capabilities.isNotEmpty()) s.copy(phase = Phase.READY, version = hs.version) else s.copy(phase = Phase.FAILED)
    /** 重试退避：1500ms * attempts，超 MAX_RETRY 转失败。 */
    fun backoffMs(s: Session): Long = (ExternalEngine.CONNECT_TIMEOUT_MS * (s.attempts + 1)).coerceAtLeast(0)
    fun canRetry(s: Session): Boolean = s.attempts < ExternalEngine.MAX_RETRY && s.phase != Phase.READY
    fun nextSeq(s: Session): Session = s.copy(seq = s.seq + 1)
    fun encodeChecked(s: Session, text: String): ByteArray =
        if (s.phase == Phase.READY) ExternalEngine.encode(text) else ByteArray(0)
    fun maxPayload(): Int = ExternalEngine.MAX_BYTES
}
