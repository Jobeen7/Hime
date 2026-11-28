// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 悬浮键盘状态机（纯函数）：可见性、位置、缩放的不可变状态与变换。
 * 视图/窗口创建由 Android 侧承载，本模块只管数据，满足性能红线：
 * 无 Android 依赖、无分配热点（返回新值，调用方按需替换）。
 */
object FloatingState {
    data class State(
        val visible: Boolean = false,
        val frame: FloatingKeyboard.Frame = FloatingKeyboard.Frame(0, 0, 0, 0),
        val scale: Float = FloatingKeyboard.SCALE_MEDIUM,
    )
    fun show(screenW: Int, screenH: Int, baseHeight: Int, scale: Float = FloatingKeyboard.SCALE_MEDIUM): State {
        val (w, h) = FloatingKeyboard.sized(scale, screenW, baseHeight)
        val x = ((screenW - w) / 2).coerceAtLeast(FloatingKeyboard.MARGIN)
        val y = (screenH - h - FloatingKeyboard.MARGIN * 2).coerceAtLeast(FloatingKeyboard.MARGIN)
        return State(visible = true, frame = FloatingKeyboard.clamp(x, y, w, h, screenW, screenH), scale = scale)
    }
    fun hide(s: State): State = s.copy(visible = false)
    fun move(s: State, dx: Int, dy: Int, screenW: Int, screenH: Int): State =
        s.copy(frame = FloatingKeyboard.clamp(s.frame.x + dx, s.frame.y + dy, s.frame.width, s.frame.height, screenW, screenH))
    fun resize(s: State, scale: Float, screenW: Int, baseHeight: Int): State {
        val sc = scale.coerceIn(FloatingKeyboard.SCALE_SMALL, FloatingKeyboard.SCALE_LARGE)
        val (w, h) = FloatingKeyboard.sized(sc, screenW, baseHeight)
        return s.copy(scale = sc, frame = FloatingKeyboard.clamp(s.frame.x, s.frame.y, w, h, screenW, Int.MAX_VALUE))
    }
    /** 磁吸：贴边阈值内吸到最近边，否则居中。 */
    fun snap(s: State, screenW: Int, threshold: Int = 40): State {
        val f = s.frame
        val newX = when {
            f.x <= threshold -> FloatingKeyboard.MARGIN
            f.x + f.width >= screenW - threshold -> screenW - f.width - FloatingKeyboard.MARGIN
            else -> f.x
        }
        return s.copy(frame = f.copy(x = newX.coerceIn(FloatingKeyboard.MARGIN, maxOf(FloatingKeyboard.MARGIN, screenW - f.width - FloatingKeyboard.MARGIN))))
    }
}
