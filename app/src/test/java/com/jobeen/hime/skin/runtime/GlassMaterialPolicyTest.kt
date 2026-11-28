// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.runtime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GlassMaterialPolicyTest {

    @Test fun blurBackends() {
        assertEquals(GlassMaterialPolicy.BlurBackend.SYSTEM_BLUR, GlassMaterialPolicy.blurBackend(36, true))
        assertEquals(GlassMaterialPolicy.BlurBackend.WINDOW_BLUR_RADIUS, GlassMaterialPolicy.blurBackend(36, false))
        assertEquals(GlassMaterialPolicy.BlurBackend.WINDOW_BLUR_RADIUS, GlassMaterialPolicy.blurBackend(31, false))
        assertEquals(GlassMaterialPolicy.BlurBackend.NONE, GlassMaterialPolicy.blurBackend(30, true))
    }

    @Test fun dragReducesBlur() {
        assertEquals(10, GlassMaterialPolicy.effectiveBlurRadius(false, 10))
        assertEquals(5, GlassMaterialPolicy.effectiveBlurRadius(true, 10))
        assertEquals(0, GlassMaterialPolicy.effectiveBlurRadius(true, -5))
    }

    @Test fun materialToggles() {
        val m = GlassMaterialPolicy.material(grainOn = true, borderOn = false)
        assertTrue(m.grainAlpha > 0f)
        assertEquals(0f, m.gradientBorderAlpha, 0.001f)
        assertEquals(160L, GlassMaterialPolicy.BLUR_RESTORE_DELAY_MS)
    }

    @Test fun themeIndex() {
        assertEquals(0, GlassMaterialPolicy.nextThemeIndex(0, 3, false, true))
        assertEquals(1, GlassMaterialPolicy.nextThemeIndex(0, 3, true, true))
        assertEquals(0, GlassMaterialPolicy.nextThemeIndex(0, 3, true, false))
        assertEquals(2, GlassMaterialPolicy.nextThemeIndex(5, 3, false, false))
    }

    @Test fun fontFallback() {
        assertEquals("sans-serif", GlassMaterialPolicy.fontFallback(listOf("", "sans-serif", "serif")))
        assertNull(GlassMaterialPolicy.fontFallback(listOf("", "  ")))
        assertNull(GlassMaterialPolicy.fontFallback(emptyList()))
    }
}
