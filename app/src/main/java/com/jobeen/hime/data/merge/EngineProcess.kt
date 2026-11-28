// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 引擎进程管理策略（纯函数）：重启退避、最大尝试、pid有效性；不启动真实进程。 */
object EngineProcess {
    const val MAX_ATTEMPTS = 3
    const val BASE_DELAY_MS = 500L
    fun shouldRestart(attempt: Int, cleanExit: Boolean): Boolean = !cleanExit && attempt in 0 until MAX_ATTEMPTS
    fun delayMs(attempt: Int): Long = BASE_DELAY_MS * (attempt + 1)
    fun pidValid(pid: Int): Boolean = pid > 0
    enum class Action { STOP, RESTART }
    fun onExit(attempt: Int, cleanExit: Boolean): Action = if (shouldRestart(attempt, cleanExit)) Action.RESTART else Action.STOP
}
