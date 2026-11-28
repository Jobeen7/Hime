// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 视图渲染接线（纯函数）：把窗口状态映射为视图参数，触摸flags/透明/尺寸，未创建真实View。 */
object RenderBind {
    data class Params(val x: Int, val y: Int, val w: Int, val h: Int, val alpha: Float, val touchMode: Int)
    const val TOUCH_LOCKED = 1
    const val TOUCH_PASSTHROUGH = 2
    const val TOUCH_DRAGGABLE = 0
    fun bind(s: FloatingWindowView.WindowState, screenW: Int, screenH: Int): Params {
        val f = FloatingKeyboard.clamp(s.x, s.y, s.w, s.h, screenW, screenH)
        val mode = when { s.locked && s.passthrough -> TOUCH_LOCKED or TOUCH_PASSTHROUGH; s.locked -> TOUCH_LOCKED; s.passthrough -> TOUCH_PASSTHROUGH; else -> TOUCH_DRAGGABLE }
        return Params(f.x, f.y, f.width, f.height, s.alpha.coerceIn(0.35f, 1f), mode)
    }
    fun visible(p: Params): Boolean = p.w > 0 && p.h > 0 && p.alpha > 0f
}
