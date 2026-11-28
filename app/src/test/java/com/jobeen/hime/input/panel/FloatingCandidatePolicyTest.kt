// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.input.panel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FloatingCandidatePolicyTest {

    @Test fun frameClampedInScreen() {
        val f = FloatingCandidatePolicy.floatingFrame(-500, -500, 400, 80, 1080, 2400)
        assertTrue(f.x >= 8 && f.y >= 8 && f.x + f.w <= 1080 && f.y + f.h <= 2400)
    }

    @Test fun frameBelowWhenNeeded() {
        val f = FloatingCandidatePolicy.floatingFrame(100, 100, 400, 80, 1080, 2400, above = true)
        assertTrue(f.y >= 8)
    }

    @Test fun gestures() {
        assertEquals(FloatingCandidatePolicy.Gesture.SELECT, FloatingCandidatePolicy.gestureOf(0, -60, 100, false, 5))
        assertEquals(FloatingCandidatePolicy.Gesture.LOWER_WEIGHT, FloatingCandidatePolicy.gestureOf(2, 60, 100, false, 5))
        assertEquals(FloatingCandidatePolicy.Gesture.NONE, FloatingCandidatePolicy.gestureOf(0, 0, 100, false, 5))
        assertEquals(FloatingCandidatePolicy.Gesture.NONE, FloatingCandidatePolicy.gestureOf(0, -60, 100, false, 0))
    }

    @Test fun longPressFirstVsLast() {
        assertEquals(FloatingCandidatePolicy.Gesture.PRESS_FIRST, FloatingCandidatePolicy.gestureOf(0, 0, 600, true, 5))
        assertEquals(FloatingCandidatePolicy.Gesture.PRESS_LAST, FloatingCandidatePolicy.gestureOf(0, 0, 900, true, 5))
    }

    @Test fun staleGuard() {
        assertTrue(FloatingCandidatePolicy.isStaleRevision(10, 9, 999))
        assertTrue(FloatingCandidatePolicy.isStaleRevision(10, 10, 50))
        assertFalse(FloatingCandidatePolicy.isStaleRevision(10, 10, 200))
        assertFalse(FloatingCandidatePolicy.isStaleRevision(10, 11, 0))
    }

    @Test fun sentenceLearningGate() {
        assertTrue(FloatingCandidatePolicy.shouldSuggestSentence(3, 7, false, true))
        assertFalse(FloatingCandidatePolicy.shouldSuggestSentence(2, 7, false, true))
        assertFalse(FloatingCandidatePolicy.shouldSuggestSentence(3, 7, true, true))
        assertFalse(FloatingCandidatePolicy.shouldSuggestSentence(3, 7, false, false))
        assertFalse(FloatingCandidatePolicy.shouldSuggestSentence(3, 8, false, true))
    }
}
