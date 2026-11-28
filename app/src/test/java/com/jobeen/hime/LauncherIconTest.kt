// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime

import com.jobeen.hime.ui.theme.LauncherIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 桌面图标：42 变体清单、默认风青、未知回落、单选组件全集。 */
class LauncherIconTest {
    @Test
    fun hasExactly42UniqueSlugs() {
        assertEquals(42, LauncherIcon.SLUGS.size)
        assertEquals(42, LauncherIcon.SLUGS.toSet().size)
    }

    @Test
    fun defaultIsFengCyan() {
        assertEquals("feng_tile_cyan", LauncherIcon.DEFAULT_SLUG)
        assertEquals("feng_tile_cyan", LauncherIcon.parse(null))
        assertEquals("feng_tile_cyan", LauncherIcon.parse(""))
        assertEquals("feng_tile_cyan", LauncherIcon.parse("bogus"))
        assertEquals("feng_tile_cyan", LauncherIcon.parse("feng_tile_cyan"))
    }

    @Test
    fun allSlugsRoundTrip() {
        for (s in LauncherIcon.SLUGS) assertEquals(s, LauncherIcon.parse(s))
    }

    @Test
    fun componentSetCovers42PlusLegacy() {
        assertEquals(43, LauncherIcon.ALL_COMPONENTS.size)
        assertEquals(43, LauncherIcon.ALL_COMPONENTS.toSet().size)
        assertTrue(LauncherIcon.ALL_COMPONENTS.contains(".ui.LauncherAlias"))
        assertTrue(LauncherIcon.ALL_COMPONENTS.contains(".ui.IconAlias_feng_tile_cyan"))
        assertTrue(LauncherIcon.ALL_COMPONENTS.contains(".ui.IconAlias_xiang_tile_mint"))
    }
}
