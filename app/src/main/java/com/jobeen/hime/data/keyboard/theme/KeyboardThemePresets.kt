package com.jobeen.hime.data.keyboard.theme

import android.graphics.Color

object KeyboardThemePresets {

    @Volatile
    var customThemes: List<KeyboardTheme> = emptyList()
        private set

    fun setCustomThemes(themes: List<KeyboardTheme>) {
        customThemes = themes
    }

    val Amoled = KeyboardTheme(
        id = "amoled",
        name = "暗夜",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.rgb(58, 58, 60),
            keyPressed = Color.rgb(72, 72, 74),
            // 皮肤深色按键无描边（键面纯色＋间距分隔），描边清零。
            keyBorderStroke = Color.argb(0, 0, 0, 0),
            specialKeyBackground = Color.rgb(58, 58, 60),
            specialKeyPressed = Color.rgb(72, 72, 74),
            specialKeyBorderStroke = Color.argb(0, 0, 0, 0),
            accentKeyBackground = Color.rgb(156, 130, 74),
            accentKeyPressed = Color.rgb(72, 72, 74),
            // 2026-11-05：内置描边全部去掉，回车强调键描边亦清零。
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(255, 255, 255),
            specialKeyText = Color.rgb(255, 255, 255),
            accentKeyText = Color.rgb(255, 255, 255),
            // 皮肤深色次要文字（九键数字角标）#FFFFFF66。
            altText = Color.argb(102, 255, 255, 255),
            // 皮肤深色键盘底 #00000003，近纯黑。
            background = Color.rgb(0, 0, 0),
            panel = KeyboardColors.ColorScheme.PanelColors(
                // 与键盘底同黑，否则候选条与键区之间出一道深灰接缝；
                // 皮肤工具栏/候选面板底同样是 #00000003。
                background = Color.rgb(0, 0, 0),
                toolbarText = Color.rgb(255, 255, 255),
                toolbarActived = Color.rgb(10, 132, 255), // #0A84FF，与长按弹窗高亮同值（BaseKeyboard 的 LONG_PRESS_HIGHLIGHT_BLUE）
                toolbarIcon = Color.rgb(255, 255, 255),
                // 工具栏按压是半透明涟漪（峰值 alpha 160，见
                // KawaiiPanelView.startPressAnimation）叠在面板底 22 灰上，
                // 取键面 pressed 的 72 灰混合后对比仅 1.33、肉眼不可辨；
                // 取 specialKeyText 纯白，混合后约 7.6:1
                toolbarPressed = Color.rgb(255, 255, 255),
                candidateBackground = Color.argb(220, 22, 22, 22),
                candidateText = Color.rgb(255, 255, 255),
                candidateIndex = Color.rgb(130, 130, 130),
                candidateDivider = Color.argb(120, 130, 130, 130),
                selectedCandidateText = Color.rgb(35, 200, 145),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.rgb(22, 22, 22),
                textColor = Color.rgb(255, 255, 255),
                secondaryTextColor = Color.rgb(120, 120, 120),
            ),
            toastBackground = Color.rgb(36, 36, 38),
            toastText = Color.rgb(245, 245, 247),
        ),
    )

    val Light = KeyboardTheme(
        id = "light",
        name = "素白",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.rgb(255, 255, 255),
            keyPressed = Color.rgb(230, 230, 230),
            // 2026-11-05：内置描边全部去掉（含回车强调键），三项描边透明。
            keyBorderStroke = Color.argb(0, 0, 0, 0),
            specialKeyText = Color.rgb(60, 60, 65),
            specialKeyBackground = Color.rgb(255, 255, 255),
            specialKeyPressed = Color.rgb(230, 230, 230),
            specialKeyBorderStroke = Color.argb(0, 0, 0, 0),
            accentKeyBackground = Color.rgb(156, 130, 74),
            accentKeyPressed = Color.rgb(140, 114, 62),
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(0, 0, 0),
            accentKeyText = Color.rgb(255, 255, 255),
            altText = Color.rgb(120, 120, 125),
            background = Color.rgb(225, 226, 231),
            surfaceStyle = KeyboardColors.SurfaceStyle.Raised,
            panel = KeyboardColors.ColorScheme.PanelColors(
                background = Color.rgb(225, 226, 231),
                toolbarText = Color.rgb(20, 20, 20),
                toolbarActived = Color.rgb(10, 132, 255), // #0A84FF，与长按弹窗高亮同值（BaseKeyboard 的 LONG_PRESS_HIGHLIGHT_BLUE）
                toolbarIcon = Color.rgb(80, 80, 85),
                candidateBackground = Color.rgb(255, 255, 255),
                candidateText = Color.rgb(20, 20, 20),
                candidateIndex = Color.rgb(120, 120, 125),
                candidateDivider = Color.argb(100, 140, 140, 145),
                selectedCandidateText = Color.rgb(35, 200, 145),
                // 同暗夜：涟漪叠在面板底 225 灰上，230 与底几乎同色
                // （混合后约 1.0:1、不可见）；墨色取正文 keyText 纯黑、
                // 峰值 alpha 160，混合后约 5.8:1（alpha 120 时用深灰
                // 60,60,65 只有约 2.3:1，低于 3:1）
                toolbarPressed = Color.rgb(0, 0, 0),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.rgb(255, 255, 255),
                textColor = Color.rgb(20, 20, 20),
                secondaryTextColor = Color.rgb(130, 130, 135),
            ),
            toastBackground = Color.rgb(45, 45, 48),
            toastText = Color.rgb(255, 255, 255),
        ),
    )

    val Sunset = KeyboardTheme(
        id = "sunset",
        name = "落日",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.rgb(245, 238, 230),
            keyPressed = Color.rgb(230, 215, 200),
            // 2026-11-05：内置描边全部去掉（含回车强调键），三项描边透明。
            keyBorderStroke = Color.argb(0, 0, 0, 0),
            specialKeyBackground = Color.rgb(245, 238, 230),
            specialKeyPressed = Color.rgb(230, 215, 200),
            specialKeyBorderStroke = Color.argb(0, 0, 0, 0),
            accentKeyBackground = Color.rgb(242, 100, 25),
            accentKeyPressed = Color.rgb(220, 85, 15),
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(50, 40, 35),
            specialKeyText = Color.rgb(120, 100, 90),
            accentKeyText = Color.rgb(255, 255, 255),
            altText = Color.rgb(140, 120, 110),
            background = Color.rgb(250, 245, 240),
            surfaceStyle = KeyboardColors.SurfaceStyle.Raised,
            panel = KeyboardColors.ColorScheme.PanelColors(
                background = Color.rgb(242, 235, 228),
                toolbarText = Color.rgb(50, 40, 35),
                toolbarActived = Color.rgb(10, 132, 255), // #0A84FF，与长按弹窗高亮同值（BaseKeyboard 的 LONG_PRESS_HIGHLIGHT_BLUE）
                toolbarIcon = Color.rgb(120, 100, 90),
                candidateBackground = Color.rgb(255, 255, 255),
                candidateText = Color.rgb(50, 40, 35),
                candidateIndex = Color.rgb(140, 120, 110),
                candidateDivider = Color.argb(100, 220, 200, 185),
                selectedCandidateText = Color.rgb(35, 200, 145),
                // 同上：原值与面板底 242,235,228 几乎同色（混合后约
                // 1.1:1）；墨色取正文 keyText 50,40,35、峰值 alpha 160，
                // 混合后约 4.1:1（暖灰 120,100,90 在 alpha 120 时只有
                // 约 1.9:1，低于 3:1）
                toolbarPressed = Color.rgb(50, 40, 35),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.rgb(245, 238, 230),
                textColor = Color.rgb(50, 40, 35),
                secondaryTextColor = Color.rgb(120, 100, 90),
            ),
            toastBackground = Color.rgb(60, 50, 45),
            toastText = Color.rgb(255, 255, 255),
        ),
    )

    val Monet = KeyboardTheme(
        id = MonetPalette.ID,
        name = "莫奈色",
        colors = MonetPalette.LightFallback,
    )

    // ---- 原生皮肤（对照输入法效果原生实现，不含任何第三方资产）----

    /** 风景图片风：整键盘背景图 + 半透明键面，图片走内置资产 scenic。 */
    val Scenic = KeyboardTheme(
        id = "native_scenic",
        name = "风景图片",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.argb(210, 255, 255, 255),
            keyPressed = Color.argb(230, 210, 225, 245),
            keyBorderStroke = Color.argb(0, 0, 0, 0),
            specialKeyBackground = Color.argb(210, 240, 245, 255),
            specialKeyPressed = Color.argb(230, 200, 215, 240),
            specialKeyBorderStroke = Color.argb(0, 0, 0, 0),
            accentKeyBackground = Color.rgb(70, 130, 200),
            accentKeyPressed = Color.rgb(55, 110, 180),
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(25, 45, 80),
            specialKeyText = Color.rgb(40, 70, 110),
            accentKeyText = Color.rgb(255, 255, 255),
            altText = Color.rgb(100, 130, 170),
            background = Color.rgb(120, 160, 210),
            backgroundImageAsset = "scenic",
            backgroundImageMode = "fill",
            panel = KeyboardColors.ColorScheme.PanelColors(
                background = Color.argb(170, 230, 240, 255),
                toolbarText = Color.rgb(25, 45, 80),
                toolbarActived = Color.rgb(70, 130, 200),
                toolbarIcon = Color.rgb(60, 90, 130),
                candidateBackground = Color.argb(210, 255, 255, 255),
                candidateText = Color.rgb(25, 45, 80),
                candidateIndex = Color.rgb(100, 130, 170),
                candidateDivider = Color.argb(120, 160, 190, 220),
                selectedCandidateText = Color.rgb(70, 130, 200),
                toolbarPressed = Color.rgb(25, 45, 80),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.argb(220, 255, 255, 255),
                textColor = Color.rgb(25, 45, 80),
                secondaryTextColor = Color.rgb(100, 130, 170),
            ),
            toastBackground = Color.rgb(50, 75, 110),
            toastText = Color.rgb(255, 255, 255),
        ),
    )

    /** 玻璃拟态：半透明底 + 系统背景模糊（低版本回落半透明）。 */
    val Glass = KeyboardTheme(
        id = "native_glass",
        name = "玻璃拟态",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.argb(140, 255, 255, 255),
            keyPressed = Color.argb(180, 220, 235, 255),
            keyBorderStroke = Color.argb(60, 255, 255, 255),
            specialKeyBackground = Color.argb(120, 240, 248, 255),
            specialKeyPressed = Color.argb(170, 210, 230, 255),
            specialKeyBorderStroke = Color.argb(60, 255, 255, 255),
            accentKeyBackground = Color.argb(220, 90, 160, 230),
            accentKeyPressed = Color.argb(235, 70, 140, 215),
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(245, 250, 255),
            specialKeyText = Color.rgb(230, 242, 255),
            accentKeyText = Color.rgb(255, 255, 255),
            altText = Color.argb(170, 220, 235, 255),
            background = Color.rgb(35, 55, 85),
            glassEnabled = true,
            panel = KeyboardColors.ColorScheme.PanelColors(
                background = Color.argb(120, 35, 55, 85),
                toolbarText = Color.rgb(245, 250, 255),
                toolbarActived = Color.rgb(140, 200, 250),
                toolbarIcon = Color.rgb(220, 235, 255),
                candidateBackground = Color.argb(150, 255, 255, 255),
                candidateText = Color.rgb(30, 50, 80),
                candidateIndex = Color.rgb(160, 190, 225),
                candidateDivider = Color.argb(100, 255, 255, 255),
                selectedCandidateText = Color.rgb(90, 160, 230),
                toolbarPressed = Color.rgb(255, 255, 255),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.argb(190, 45, 65, 95),
                textColor = Color.rgb(245, 250, 255),
                secondaryTextColor = Color.rgb(170, 200, 230),
            ),
            toastBackground = Color.rgb(45, 65, 95),
            toastText = Color.rgb(245, 250, 255),
        ),
    )

    /** 简约风：素色扁平、低圆角、无图片与模糊。 */
    val Minimal = KeyboardTheme(
        id = "native_minimal",
        name = "简约",
        colors = KeyboardColors.ColorScheme(
            keyBackground = Color.rgb(250, 250, 250),
            keyPressed = Color.rgb(235, 235, 235),
            keyBorderStroke = Color.argb(0, 0, 0, 0),
            specialKeyBackground = Color.rgb(245, 245, 245),
            specialKeyPressed = Color.rgb(230, 230, 230),
            specialKeyBorderStroke = Color.argb(0, 0, 0, 0),
            accentKeyBackground = Color.rgb(50, 110, 210),
            accentKeyPressed = Color.rgb(40, 95, 190),
            accentKeyBorderStroke = Color.argb(0, 0, 0, 0),
            keyText = Color.rgb(35, 35, 40),
            specialKeyText = Color.rgb(90, 90, 100),
            accentKeyText = Color.rgb(255, 255, 255),
            altText = Color.rgb(130, 130, 140),
            background = Color.rgb(242, 242, 245),
            surfaceStyle = KeyboardColors.SurfaceStyle.Flat,
            cornerRadius = 2f,
            panel = KeyboardColors.ColorScheme.PanelColors(
                background = Color.rgb(242, 242, 245),
                toolbarText = Color.rgb(35, 35, 40),
                toolbarActived = Color.rgb(50, 110, 210),
                toolbarIcon = Color.rgb(110, 110, 120),
                candidateBackground = Color.rgb(255, 255, 255),
                candidateText = Color.rgb(35, 35, 40),
                candidateIndex = Color.rgb(130, 130, 140),
                candidateDivider = Color.argb(100, 180, 180, 190),
                selectedCandidateText = Color.rgb(50, 110, 210),
                toolbarPressed = Color.rgb(35, 35, 40),
            ),
            pinner = KeyboardColors.ColorScheme.PinnerColors(
                background = Color.rgb(255, 255, 255),
                textColor = Color.rgb(35, 35, 40),
                secondaryTextColor = Color.rgb(130, 130, 140),
            ),
            toastBackground = Color.rgb(60, 60, 68),
            toastText = Color.rgb(255, 255, 255),
        ),
    )

    val ALL: List<KeyboardTheme>
        get() = listOf(
            Amoled,
            Light,
            Sunset,
            Monet,
            Scenic,
            Glass,
            Minimal,
        ) + customThemes
}