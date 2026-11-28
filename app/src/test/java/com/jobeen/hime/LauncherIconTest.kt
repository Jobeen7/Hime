// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime

import com.jobeen.hime.ui.theme.LauncherIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 桌面图标变体纯逻辑：42 变体清单、默认变体、alias 类名对应。 */
class LauncherIconTest {

    @Test
    fun variants_areExactly42UniqueSlugs() {
        assertEquals(42, LauncherIcon.VARIANTS.size)
        val slugs = LauncherIcon.VARIANTS.map { it.slug }
        assertEquals(42, slugs.toSet().size)
    }

    @Test
    fun variants_coverThreeFormsTimesFourteenColors() {
        val slugs = LauncherIcon.VARIANTS.map { it.slug }.toSet()
        for (form in listOf("single", "diagonal", "triple")) {
            for (color in listOf(
                "wbg_teal", "wbg_blue", "wbg_ruby", "wbg_orange",
                "tile_black", "tile_blue", "tile_cyan", "tile_lightblue",
                "tile_orange", "tile_red", "tile_violet", "tile_white",
                "tile_yellow", "tile_mint",
            )) {
                assertTrue("missing $form/$color", slugs.contains("${form}_$color"))
            }
        }
    }

    @Test
    fun defaultVariant_isSingleWbgTealCarriedByLauncherAlias() {
        assertEquals("single_wbg_teal", LauncherIcon.DEFAULT_SLUG)
        assertEquals("com.jobeen.hime.ui.LauncherAlias", LauncherIcon.LAUNCHER_ALIAS_CLASS)
        assertEquals(LauncherIcon.VARIANTS.first { it.slug == "single_wbg_teal" },
            LauncherIcon.variantForSlug("single_wbg_teal"))
        assertEquals(LauncherIcon.VARIANTS.first { it.slug == "single_wbg_teal" },
            LauncherIcon.variantForSlug("unknown_slug"))
        assertEquals(LauncherIcon.VARIANTS.first { it.slug == "single_wbg_teal" },
            LauncherIcon.variantForSlug(null))
        assertEquals(LauncherIcon.VARIANTS.first { it.slug == "single_wbg_teal" },
            LauncherIcon.variantForSlug(""))
    }

    @Test
    fun aliasClassNames_matchManifestConvention() {
        for (variant in LauncherIcon.VARIANTS) {
            assertEquals(
                "com.jobeen.hime.IconAlias_${variant.slug}",
                variant.aliasClassName,
            )
        }
        assertEquals(42, LauncherIcon.VARIANTS.map { it.aliasClassName }.toSet().size)
    }

    @Test
    fun indexForSlug_roundTripsAndDefaults() {
        for ((index, variant) in LauncherIcon.VARIANTS.withIndex()) {
            assertEquals(index, LauncherIcon.indexForSlug(variant.slug))
        }
        assertEquals(0, LauncherIcon.indexForSlug("nope"))
        assertEquals(LauncherIcon.variantForSlug("nope").label, LauncherIcon.labelForSlug("nope"))
    }
}
