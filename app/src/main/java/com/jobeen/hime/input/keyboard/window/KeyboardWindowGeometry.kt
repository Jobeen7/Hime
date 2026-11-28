// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.input.keyboard.window

import com.jobeen.hime.data.merge.FloatingKeyboard

/**
 * F07 窗口几何决策（纯函数，不持 View）：
 * 悬浮 / 单手 / 分体 / 横屏 的尺寸与位置决策，统一走 FloatingKeyboard.clamp。
 * 拖动期间窗口仅平移（translation），绝不走全窗 requestLayout 重排；
 * 具体移动在 KeyboardWindowView 的拖动层应用，本文件只算坐标。
 */
object KeyboardWindowGeometry {

    enum class Dock { LEFT, RIGHT }

    enum class WindowMode { FULL, FLOATING, SINGLE_HAND, SPLIT, LANDSCAPE_FLOAT }

    data class Margins(val left: Int, val right: Int)

    /** 决策：屏幕横屏或宽度超阈值 -> LANDSCAPE_FLOAT / SPLIT；单手开 -> SINGLE_HAND。 */
    fun decide(
        screenW: Int,
        screenH: Int
        isLandscape: Boolean,
        singleHand: Boolean,
        splitEnabled: Boolean,
        splitThresholdPx: Int
    ): WindowMode = when {
        isLandscape -> WindowMode.LANDSCAPE_FLOAT
        splitEnabled && screenW >= splitThresholdPx -> WindowMode.SPLIT
        singleHand -> WindowMode.SINGLE_HAND
        else -> WindowMode.FULL
    }

    /** 拖动中绝不触发整窗重排：拖动层只改 translationX/Y，松手后才可重排。 */
    fun needsRelayoutDuringDrag(): Boolean = false

    /** 悬浮框：位置钳制到屏内，复用 FloatingKeyboard.clamp。 */
    fun floatingFrame(
        x: Int, y: Int, w: Int, h: Int, screenW: Int, screenH: Int
    ): FloatingKeyboard.Frame = FloatingKeyboard.clamp(x, y, w, h, screenW, screenH)

    /** 拖动平移后的钳制框。 */
    fun draggedFrame(
        current: FloatingKeyboard.Frame, dx: Int, dy: Int, screenW: Int, screenH: Int
    ): FloatingKeyboard.Frame =
        FloatingKeyboard.clamp(current.x + dx, current.y + dy, current.width, current.height, screenW, screenH)

    /** 双角缩放：按缩放系数改宽高，钳制在缩放区间内，锚点钳制进屏。 */
    fun cornerScaled(
        frame: FloatingKeyboard.Frame, scale: Float, screenW: Int, screenH: Int
    ): FloatingKeyboard.Frame {
        val s = scale.coerceIn(0.5f, 2.0f)
        val w = (frame.width * s).toInt().coerceAtLeast(200)
        val h = (frame.height * s).toInt().coerceAtLeast(120)
        return FloatingKeyboard.clamp(frame.x, frame.y, w, h, screenW, screenH)
    }

    /** 旋转：仅视觉旋转，布局框不变；角度归一到 [-30, 30] 避免失效。 */
    fun clampRotationDegrees(deg: Float): Float = deg.coerceIn(-30f, 30f)

    /** 单手宽度：40%–100% 宽度区间，钳制 200–1200。 */
    fun singleHandWidth(screenW: Int, percent: Float): Int {
        val p = percent.coerceIn(0.4f, 1.0f)
        return (screenW * p).toInt().coerceIn(200, 1200)
    }

    /** 单手靠边 x。 */
    fun singleHandX(screenW: Int, width: Int, dock: Dock): Int = when (dock) {
        Dock.LEFT -> FloatingKeyboard.MARGIN
        Dock.RIGHT -> (screenW - width - FloatingKeyboard.MARGIN).coerceAtLeast(FloatingKeyboard.MARGIN)
    }

    /** 分体是否触发。 */
    fun shouldSplit(screenW: Int, splitEnabled: Boolean, thresholdPx: Int): Boolean =
        splitEnabled && screenW >= thresholdPx.coerceAtLeast(600)

    /** 分体左右半区框：逐行拆点以总宽与间隙计算后钳制。 */
    fun splitFrames(
        screenW: Int, screenH: Int leftW: Int, rightW: Int, gapPx: Int, y: Int, h: Int,
    ): Pair<FloatingKeyboard.Frame, FloatingKeyboard.Frame> {
        val gap = gapPx.coerceIn(0, 200)
        val totalW = leftW + rightW + gap
        val startX = ((screenW - totalW) / 2).coerceAtLeast(FloatingKeyboard.MARGIN)
        val left = FloatingKeyboard.clamp(startX, y, leftW, h, screenW, screenH)
        val right = FloatingKeyboard.clamp(startX + leftW + gap, y, rightW, h, screenW, screenH)
        return left to right
    }
}
