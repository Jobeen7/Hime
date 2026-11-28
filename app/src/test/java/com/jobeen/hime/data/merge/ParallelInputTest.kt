// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ParallelInputTest {
    @Test fun neighbor_needsThree() {
        NeighborCorrection.clearAll()
        NeighborCorrection.recordMistake('s', 'j'); NeighborCorrection.recordMistake('s', 'j')
        assertEquals('s', NeighborCorrection.correct('s'))
        NeighborCorrection.recordMistake('s', 'j')
        assertEquals('j', NeighborCorrection.correct('s'))
        NeighborCorrection.clearAll()
    }
    @Test fun fuzzy_emptyAndLatin() {
        assertEquals("", FuzzySound.normalize(""))
        assertEquals("hello", FuzzySound.normalize("hello"))
    }
    @Test fun schemes_eight() {
        assertEquals(8, SchemePresets.all.size)
        assertNotNull(SchemePresets.byId("wanxiang"))
        assertNull(SchemePresets.byId("nope"))
    }
}
