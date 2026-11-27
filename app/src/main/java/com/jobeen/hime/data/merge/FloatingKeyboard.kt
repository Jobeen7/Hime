// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 悬浮键盘（纯函数）：位置钳制与尺寸预设。所有坐标做屏幕边界
 * 钳制，避免键盘漂到屏幕外或盖住宿主应用输入区；
 * 尺寸分三档（小/中/大），按宽度比例换算，高度自动跟随。
 * 纯几何计算，不创建视图，不占额外内存。
 */
object FloatingKeyboard {

    const val MARGIN = 8
    const val SCALE_SMALL = 0.5f
    const val SCALE_MEDIUM = 0.72f
    const val SCALE_LARGE = 0.92f

    data class Frame(val x: Int, val y: Int, val width: Int, val height: Int)

    fun clamp(x: Int, y: Int, w: Int, h: Int, screenW: Int, screenH: Int): Frame {
        val safeW = if (w <= 0) 320 else w
        val safeH = if (h <= 0) 220 else h
        val maxX = maxOf(MARGIN, screenW - safeW - MARGIN)
        val maxY = maxOf(MARGIN, screenH - safeH - MARGIN)
        return Frame(x.coerceIn(MARGIN, maxX), y.coerceIn(MARGIN, maxY), safeW, safeH)
    }

    /** 按缩放档位计算悬浮键盘宽高。 */
    fun sized(scale: Float, screenW: Int, baseHeight: Int): Pair<Int, Int> {
        val s = when {
            scale <= SCALE_SMALL -> SCALE_SMALL
            scale >= SCALE_LARGE -> SCALE_LARGE
            else -> SCALE_MEDIUM
        }
        val w = (screenW * s).toInt().coerceIn(200, 1200)
        val h = (baseHeight * s).toInt().coerceIn(120, 480)
        return w to h
    }
}
