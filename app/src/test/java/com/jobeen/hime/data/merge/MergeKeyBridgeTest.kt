package com.jobeen.hime.data.merge

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

/** 键序桥单测（null Context 走纯函数路径，不读偏好）。 */
class MergeKeyBridgeTest {

    @After
    fun tearDown() {
        NeighborCorrection.clearAll()
    }

    @Test
    fun emptyAndNonLetter_passthrough() {
        assertEquals("", MergeKeyBridge.normalizeSequence(null, ""))
        assertEquals("123", MergeKeyBridge.normalizeSequence(null, "123"))
    }

    @Test
    fun neighborLearned_appliesAfterThreshold() {
        repeat(3) { NeighborCorrection.recordMistake('g', 'f') }
        assertEquals("f", MergeKeyBridge.normalizeSequence(null, "g"))
    }

    @Test
    fun fuzzy_zh_to_z() {
        assertEquals("zuang", MergeKeyBridge.normalizeSequence(null, "zhuang"))
    }
}
