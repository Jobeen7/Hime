// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NeighborCorrectionF02Test {
    @Test fun distance_neighborIsOne() {
        assertEquals(0, NeighborCorrection.distance('a', 'a'))
        assertEquals(1, NeighborCorrection.distance('a', 's'))
    }
    @Test fun alternatives_secondSlot_hasNeighbors() {
        val alts = NeighborCorrection.alternativesFor('a', "wanxiang")
        assertTrue(alts.isNotEmpty())
        assertTrue(alts.contains('s'))
    }
    @Test fun perScheme_isolated() {
        NeighborCorrection.clearScheme("wanxiang")
        NeighborCorrection.clearScheme("xiaohe")
        repeat(3) { NeighborCorrection.recordMistake("wanxiang", 'a', 's') }
        assertEquals('s', NeighborCorrection.learnedForScheme("wanxiang")['a'])
        assertFalse(NeighborCorrection.learnedForScheme("xiaohe").containsKey('a'))
    }
    @Test fun syllable_expandThreshold() {
        val s = NeighborCorrection.splitSyllables("nihao")
        assertTrue(s.isNotEmpty())
        assertFalse(NeighborCorrection.needsExpand(listOf("ni")))
        assertTrue(NeighborCorrection.needsExpand(listOf("a", "b", "c")))
    }
    @Test fun window_clamps() {
        val w = NeighborCorrection.syllableWindow(listOf("a", "b", "c", "d"), 1, 2)
        assertEquals(listOf("b", "c"), w)
        assertTrue(NeighborCorrection.syllableWindow(listOf("a"), 5).isEmpty())
    }
    @Test fun rhyme_customWins() {
        assertEquals("X", NeighborCorrection.rhymeTip('a', mapOf('a' to "X")))
        assertEquals("ɑ", NeighborCorrection.rhymeTip('a', emptyMap()))
    }
}
