// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 真实推理会话守卫（纯函数）：打开/关闭状态、推理前准入、输入上限与超时；真实模型由调用方持有。 */
object InferenceReal {
    const val MAX_INPUT_BYTES = 1024 * 1024
    const val MAX_CONCURRENT = 1
    const val TIMEOUT_MS = 3000L
    enum class Phase { CLOSED, OPENING, READY }
    fun canInfer(p: Phase, bytes: Int, running: Int): Boolean =
        p == Phase.READY && bytes in 1..MAX_INPUT_BYTES && running in 0 until MAX_CONCURRENT
    fun next(p: Phase, opened: Boolean, failed: Boolean): Phase = when {
        failed -> Phase.CLOSED
        p == Phase.CLOSED && opened -> Phase.OPENING
        p == Phase.OPENING && opened -> Phase.READY
        else -> p
    }
    fun clamped(bytes: Int): Int = bytes.coerceIn(1, MAX_INPUT_BYTES)
    fun timedOut(elapsedMs: Long): Boolean = elapsedMs >= TIMEOUT_MS
}
