// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.input.panel

import com.jobeen.hime.data.merge.FloatingKeyboard
import kotlin.math.abs

/**
 * F08 浮动候选窗与手势策略（纯函数）：
 * 浮动框跟随光标并钳制进屏；候选手势（上滑选字/下滑降权/长按首末字）
 * 只做意图判定，具体上屏由候选管线执行；防误触用 revision + 最小间隔。
 */
object FloatingCandidatePolicy {

    enum class Gesture { NONE, SELECT, LOWER_WEIGHT, PRESS_FIRST, PRESS_LAST }

    data class Frame(val x: Int, val y: Int, val w: Int, val h: Int)

    /**
     * 跟随光标的浮动框：默认在光标上方；上方放不下则改到下方；最后钳制进屏。
     * 输入框勿遮挡：chosen y 始终在光标带±一带之外。
     */
    fun floatingFrame(cursorX: Int, cursorY: Int, w: Int, h: Int, screenW: Int, screenH: Int, above: Boolean = true): Frame {
        val safeW = w.coerceIn(240, 1200)
        val safeH = h.coerceIn(48, 240)
        val prefY = if (above) cursorY - safeH - 8 else cursorY + 24
        val box = FloatingKeyboard.clamp(cursorX, prefY, safeW, safeH, screenW, screenH)
        return Frame(box.x, box.y, box.width, box.height)
    }

    /** 候选区手势意图判定：纯靠位移与时长。 */
    fun gestureOf(dx: Int, dy: Int, durationMs: Long, isLongPress: Boolean, candidateCount: Int): Gesture {
        if (candidateCount <= 0) return Gesture.NONE
        if (isLongPress || durationMs >= 500) {
            return if (candidateCount > 1 && durationMs >= 800) Gesture.PRESS_LAST else Gesture.PRESS_FIRST
        }
        if (dy <= -48 && abs(dy) > abs(dx)) return Gesture.SELECT
        if (dy >= 48 && abs(dy) > abs(dx)) return Gesture.LOWER_WEIGHT
        return Gesture.NONE
    }

    /** 防快速连点误触：同 revision 列表在最小间隔内重复提交被吞掉。 */
    fun isStaleRevision(lastRevision: Long, nextRevision: Long, elapsedMs: Long, minIntervalMs: Long = 120L): Boolean {
        if (nextRevision < lastRevision) return true
        return nextRevision == lastRevision && elapsedMs < minIntervalMs
    }

    /**
     * 本地常用句学习门槛（默认关）：同一整句在 learningDays 窗口内记满 minCount 次才建议。
     * 密码框一律不学习。
     */
    fun shouldSuggestSentence(inputCount: Int, windowDays: Int, isPasswordField: Boolean, learningEnabled: Boolean): Boolean {
        if (!learningEnabled || isPasswordField) return false
        if (windowDays <= 0 || windowDays > 7) return false
        return inputCount >= 3
    }
}
