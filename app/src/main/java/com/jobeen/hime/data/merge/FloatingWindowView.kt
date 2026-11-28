// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 悬浮窗口视图层（纯函数描述）：状态、拖动、输入透传与锁定。 */
object FloatingWindowView {
    data class WindowState(val x: Int = 0, val y: Int = 0, val w: Int = 320, val h: Int = 220, val alpha: Float = 1f, val locked: Boolean = false, val passthrough: Boolean = false)
    fun move(s: WindowState, dx: Int, dy: Int): WindowState = if (s.locked) s else { val f = FloatingKeyboard.clamp(s.x + dx, s.y + dy, s.w, s.h, 1080, 2400); s.copy(x = f.x, y = f.y) }
    fun resize(s: WindowState, scale: Float): WindowState { val (w, h) = FloatingKeyboard.sized(scale, 1080, 320); val f = FloatingKeyboard.clamp(s.x, s.y, w, h, 1080, 2400); return s.copy(w = f.width, h = f.height, x = f.x, y = f.y) }
    fun toggleLock(s: WindowState): WindowState = s.copy(locked = !s.locked)
    fun setPassthrough(s: WindowState, on: Boolean): WindowState = s.copy(passthrough = on)
    fun setAlpha(s: WindowState, alpha: Float): WindowState = s.copy(alpha = alpha.coerceIn(0.35f, 1f))
    fun isVisible(s: WindowState): Boolean = s.w > 0 && s.h > 0 && s.alpha > 0f
}
