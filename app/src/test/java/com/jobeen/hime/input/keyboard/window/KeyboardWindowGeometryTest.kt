// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.input.keyboard.window

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardWindowGeometryTest {

    @Test fun dragNeverRelayouts() {
        assertFalse(KeyboardWindowGeometry.needsRelayoutDuringDrag())
    }

    @Test fun floatingClampsInside() {
        val f = KeyboardWindowGeometry.floatingFrame(-100, 9999, 400, 200, 1080, 2400)
        assertTrue(f.x >= 8)
        assertTrue(f.y >= 8)
        assertTrue(f.x + f.width <= 1080)
        assertTrue(f.y + f.height <= 2400)
    }

    @Test fun singleHandWidthBounds() {
        assertEquals(432, KeyboardWindowGeometry.singleHandWidth(1080, 0.4f))
        assertEquals(1080, KeyboardWindowGeometry.singleHandWidth(1080, 9f))
        assertEquals(432, KeyboardWindowGeometry.singleHandWidth(1080, 0.1f))
    }

    @Test fun singleHandDock() {
        assertEquals(8, KeyboardWindowGeometry.singleHandX(1080, 600, KeyboardWindowGeometry.Dock.LEFT))
        assertEquals(472, KeyboardWindowGeometry.singleHandX(1080, 600, KeyboardWindowGeometry.Dock.RIGHT))
    }

    @Test fun splitDecision() {
        assertTrue(KeyboardWindowGeometry.shouldSplit(1800, true, 1200))
        assertFalse(KeyboardWindowGeometry.shouldSplit(1080, true, 1200))
        assertFalse(KeyboardWindowGeometry.shouldSplit(1800, false, 1200))
    }

    @Test fun decideLandscapeFirst() {
        assertEquals(
            KeyboardWindowGeometry.WindowMode.LANDSCAPE_FLOAT,
            KeyboardWindowGeometry.decide(2400, 1080, true, false, true, 1200),
        )
        assertEquals(
            KeyboardWindowGeometry.WindowMode.SPLIT,
            KeyboardWindowGeometry.decide(1800, 900, false, false, true, 1200),
        )
        assertEquals(
            KeyboardWindowGeometry.WindowMode.SINGLE_HAND,
            KeyboardWindowGeometry.decide(1080, 2400, false, true, false, 1200),
        )
    }

    @Test fun cornerScaleClamped() {
        val base = KeyboardWindowGeometry.floatingFrame(100, 100, 400, 200, 1080, 2400)
        val big = KeyboardWindowGeometry.cornerScaled(base, 9f, 1080, 2400)
        assertTrue(big.width <= 1064)
        assertTrue(big.height >= 120)
    }

    @Test fun rotationClamped() {
        assertEquals(30f, KeyboardWindowGeometry.clampRotationDegrees(99f), 0.001f)
        assertEquals(-30f, KeyboardWindowGeometry.clampRotationDegrees(-99f), 0.001f)
    }

    @Test fun splitFramesNoOverlap() {
        val (l, r) = KeyboardWindowGeometry.splitFrames(1800, 900, 700, 700, 32, 40, 400)
        assertTrue(l.x + l.width <= r.x)
        assertTrue(r.x + r.width <= 1800)
    }
}
