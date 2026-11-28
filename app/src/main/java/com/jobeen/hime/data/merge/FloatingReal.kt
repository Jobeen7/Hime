// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 悬浮窗口拖动与吸附：速度、边缘吸附与透传约束（纯函数）。 */
object FloatingReal {
    const val SNAP_PX = 24
    data class Velocity(val vx: Float, val vy: Float)
    fun velocity(dx: Int, dy: Int, dtMs: Long): Velocity {
        val dt = if (dtMs <= 0L) 1L else dtMs
        return Velocity(dx * 1000f / dt, dy * 1000f / dt)
    }
    fun snap(s: FloatingWindowView.WindowState, screenW: Int, screenH: Int): FloatingWindowView.WindowState {
        var x = s.x
        var y = s.y
        if (x < SNAP_PX) x = FloatingKeyboard.MARGIN
        if (x + s.w > screenW - SNAP_PX) x = maxOf(FloatingKeyboard.MARGIN, screenW - s.w - FloatingKeyboard.MARGIN)
        if (y < SNAP_PX) y = FloatingKeyboard.MARGIN
        if (y + s.h > screenH - SNAP_PX) y = maxOf(FloatingKeyboard.MARGIN, screenH - s.h - FloatingKeyboard.MARGIN)
        val f = FloatingKeyboard.clamp(x, y, s.w, s.h, screenW, screenH)
        return s.copy(x = f.x, y = f.y)
    }
    fun dragResult(s: FloatingWindowView.WindowState, dx: Int, dy: Int): FloatingWindowView.WindowState {
        if (s.locked) return s
        val moved = FloatingWindowView.move(s, dx, dy)
        return snap(moved, 1080, 2400)
    }
    fun passthroughDepth(on: Boolean): Int = if (on) 1 else 0
}
