// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime

import com.jobeen.hime.data.keyboard.theme.KeyboardColors
import com.jobeen.hime.data.keyboard.theme.KeyboardTheme
import com.jobeen.hime.data.theme.CompactTheme
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 原生皮肤字段单测：ColorScheme 新增字段默认值、CompactTheme 往返、
 * 旧版码（无新字段）兼容解码。只用手写配色值，不触 Presets（其初始化调
 * android.graphics.Color，主机单测会抛）。
 */
class SkinNativeThemeTest {

    private fun sampleColors() = KeyboardColors.ColorScheme(
        keyBackground = 0xFF1A3F4E.toInt(),
        keyPressed = 0xFF24546A.toInt(),
        keyBorderStroke = 0xFF2F6280.toInt(),
        specialKeyBackground = 0xFF16333F.toInt(),
        specialKeyPressed = 0xFF205067.toInt(),
        specialKeyBorderStroke = 0xFF2F6280.toInt(),
        accentKeyBackground = 0xFF2EC2E6.toInt(),
        accentKeyPressed = 0xFF6BD6EF.toInt(),
        accentKeyBorderStroke = 0xFF2EC2E6.toInt(),
        keyText = 0xFFE8F6FA.toInt(),
        specialKeyText = 0xFF8FC3D4.toInt(),
        accentKeyText = 0xFF062331.toInt(),
        altText = 0xFF6FA8BC.toInt(),
        background = 0xFF0F2A33.toInt(),
        panel = KeyboardColors.ColorScheme.PanelColors(
            background = 0xFF0F2A33.toInt(),
            toolbarText = 0xFFE8F6FA.toInt(),
            toolbarActived = 0xFF2EC2E6.toInt(),
            toolbarIcon = 0xFFE8F6FA.toInt(),
            candidateBackground = 0xFF16333F.toInt(),
            candidateText = 0xFFE8F6FA.toInt(),
            candidateIndex = 0xFF6FA8BC.toInt(),
            candidateDivider = 0xFF2F6280.toInt(),
            toolbarPressed = 0xFFE8F6FA.toInt(),
        ),
        pinner = KeyboardColors.ColorScheme.PinnerColors(
            background = 0xFF16333F.toInt(),
            textColor = 0xFFE8F6FA.toInt(),
            secondaryTextColor = 0xFF6FA8BC.toInt(),
        ),
    )

    @Test
    fun `color scheme native fields default to plain background`() {
        val colors = sampleColors()
        assertNull(colors.backgroundImageAsset)
        assertEquals("fill", colors.backgroundImageMode)
        assertFalse(colors.glassEnabled)
    }

    @Test
    fun `compact theme roundtrip keeps native fields`() {
        val theme = KeyboardTheme(
            id = "native_test",
            name = "原生测试",
            colors = sampleColors().copy(
                backgroundImageAsset = "scenic",
                backgroundImageMode = "fill",
                glassEnabled = true,
            ),
        )
        val restored = CompactTheme.from(theme).toKeyboardTheme()
        assertEquals("scenic", restored.colors.backgroundImageAsset)
        assertEquals("fill", restored.colors.backgroundImageMode)
        assertTrue(restored.colors.glassEnabled)

        val json = Json { ignoreUnknownKeys = true }
        val encoded = json.encodeToString(CompactTheme.serializer(), CompactTheme.from(theme))
        val decoded = json.decodeFromString<CompactTheme>(encoded).toKeyboardTheme()
        assertEquals("scenic", decoded.colors.backgroundImageAsset)
        assertTrue(decoded.colors.glassEnabled)
    }

    @Test
    fun `old compact json without native fields decodes with defaults`() {
        val json = Json { ignoreUnknownKeys = true }
        val base = CompactTheme.from(
            KeyboardTheme(id = "old", name = "旧", colors = sampleColors()),
        )
        // 旧版码：去掉新字段后再解码
        val oldJson = json.encodeToString(CompactTheme.serializer(), base)
            .replace(Regex(""","bi":"[^"]*""""), "")
            .replace(Regex(""","bm":"[^"]*""""), "")
            .replace(Regex(""","ge":(true|false)"""), "")
        val decoded = json.decodeFromString<CompactTheme>(oldJson).toKeyboardTheme()
        assertNull(decoded.colors.backgroundImageAsset)
        assertEquals("fill", decoded.colors.backgroundImageMode)
        assertFalse(decoded.colors.glassEnabled)
    }

    @Test
    fun `blank background mode coerces to fill`() {
        val theme = KeyboardTheme(
            id = "blank_mode",
            name = "空模式",
            colors = sampleColors().copy(backgroundImageMode = ""),
        )
        val restored = CompactTheme.from(theme).toKeyboardTheme()
        assertEquals("fill", restored.colors.backgroundImageMode)
    }
}
