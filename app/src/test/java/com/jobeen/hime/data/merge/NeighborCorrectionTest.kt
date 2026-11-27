// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NeighborCorrectionTest {

    @Test
    fun correct_belowThreshold_unchanged() {
        NeighborCorrection.clearAll()
        NeighborCorrection.recordMistake('s', 'a')
        assertEquals('s', NeighborCorrection.correct('s'))
    }

    @Test
    fun correct_atThreshold_mapsToIntended() {
        NeighborCorrection.clearAll()
        repeat(3) { NeighborCorrection.recordMistake('s', 'a') }
        assertEquals('a', NeighborCorrection.correct('s'))
    }

    @Test
    fun correct_singleRecord_noDoublePut() {
        NeighborCorrection.clearAll()
        NeighborCorrection.recordMistake('k', 'j')
        assertEquals(0, NeighborCorrection.learnedMappings().size)
        // learnedMappings 只含达阈值项：单次记录不出现
        assertTrue(NeighborCorrection.learnedMappings().isEmpty())
    }

    @Test
    fun correct_unknownKey_unchanged() {
        assertEquals('q', NeighborCorrection.correct('q'))
    }
}
