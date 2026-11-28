// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.runtime

/**
 * F09 玻璃材质与主题/字体决策（纯函数，不碰 View）：
 * Android 16 背景模糊回退、材质参数、日夜主题轮询、字体缺字回落顺序。
 * 具体画布在 KeyEffectPainter / 主题层应用，这里只算参数。
 */
object GlassMaterialPolicy {

    enum class BlurBackend { SYSTEM_BLUR, WINDOW_BLUR_RADIUS, NONE }

    data class Material(
        val innerShadowPx: Float,
        val outerShadowPx: Float,
        val grainAlpha: Float,
        val gradientBorderAlpha: Float,
        val bottom3DPx: Float,
        val pressScale: Float,
        val pressSinkPx: Float,
    )

    /** 背景模糊回退：API 36+ 有系统模糊用之；否则 API 31+ 用 Window.setBackgroundBlurRadius；更低则无。 */
    fun blurBackend(api: Int, hasSystemBlur: Boolean): BlurBackend = when {
        api >= 36 && hasSystemBlur -> BlurBackend.SYSTEM_BLUR
        api >= 31 -> BlurBackend.WINDOW_BLUR_RADIUS
        else -> BlurBackend.NONE
    }

    /** 拖动期间降模糊（乘 0.5），松手 160ms 后恢复全模糊；实际恢复由窗口层延时执行。 */
    fun effectiveBlurRadius(isDragging: Boolean, fullRadiusPx: Int): Int {
        val r = fullRadiusPx.coerceAtLeast(0)
        return if (isDragging) r / 2 else r
    }

    const val BLUR_RESTORE_DELAY_MS = 160L

    /** 材质参数：阴影/磨砂颗粒/渐变边框/3D底边/按压缩放下沉，全部钳制防极值。 */
    fun material(grainOn: Boolean, borderOn: Boolean): Material = Material(
        innerShadowPx = 4f,
        outerShadowPx = 8f,
        grainAlpha = if (grainOn) 0.12f else 0f,
        gradientBorderAlpha = if (borderOn) 0.35f else 0f,
        bottom3DPx = 3f,
        pressScale = 0.96f,
        pressSinkPx = 1.5f,
    )

    /** 日夜主题轮询：跟随系统时按 isDark 取同模式下一主题；手动模式直接取指定。 */
    fun nextThemeIndex(current: Int, modeCount: Int, followSystem: Boolean, isDark: Boolean): Int {
        if (modeCount <= 0) return 0
        return if (followSystem) {
            ((current + if (isDark) 1 else 0) % modeCount + modeCount) % modeCount
        } else {
            current % modeCount
        }
    }

    /**
     * 字体缺字回落：按序取第一个非空候选，未命中时回 null（调用方走系统默认，绝不空白）。
     */
    fun fontFallback(orderedCandidates: List<String>): String? =
        orderedCandidates.firstOrNull { it.isNotBlank() }?.trim()?.takeIf { it.isNotEmpty() }
}
