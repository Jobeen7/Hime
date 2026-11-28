// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FloatingPhysicsTest {
    @Test fun clampDrag_insideStays() {
        val p = FloatingPhysics.clampDrag(100, 200, 320, 220, 1080, 2400)
        assertEquals(100, p.x); assertEquals(200, p.y)
    }
    @Test fun clampDrag_outsideClampsToMargin() {
        val p = FloatingPhysics.clampDrag(-50, 9999, 320, 220, 1080, 2400)
        assertEquals(FloatingKeyboard.MARGIN, p.x)
        assertEquals(2400 - 220 - FloatingKeyboard.MARGIN, p.y)
    }
    @Test fun snapX_leftRightCenter() {
        assertEquals(FloatingKeyboard.MARGIN, FloatingPhysics.snapX(10, 320, 1080))
        assertEquals(1080 - 320 - FloatingKeyboard.MARGIN, FloatingPhysics.snapX(1050, 320, 1080))
        assertEquals(400, FloatingPhysics.snapX(400, 320, 1080))
    }
    @Test fun hitTest_insideOutside() {
        assertTrue(FloatingPhysics.hitTest(100, 100, 0, 0, 320, 220))
        assertFalse(FloatingPhysics.hitTest(320, 100, 0, 0, 320, 220))
        assertFalse(FloatingPhysics.hitTest(100, 100, 0, 0, 0, 220))
        val f = FloatingKeyboard.Frame(10, 10, 100, 100)
        assertTrue(FloatingPhysics.hitTest(50, 50, f))
    }
    @Test fun coerceVelocity_caps() {
        val v = FloatingPhysics.coerceVelocity(200f, -200f)
        assertEquals(FloatingPhysics.MAX_VELOCITY, v.vx, 0.001f)
        assertEquals(-FloatingPhysics.MAX_VELOCITY, v.vy, 0.001f)
    }
    @Test fun fling_zeroReturnsEmpty() {
        val traj = FloatingPhysics.simulateFling(FloatingPhysics.Position(100, 100), FloatingPhysics.Velocity(0f, 0f), 320, 220, 1080, 2400)
        assertTrue(traj.isEmpty())
    }
    @Test fun fling_decaysWithinMaxSteps() {
        val traj = FloatingPhysics.simulateFling(FloatingPhysics.Position(100, 100), FloatingPhysics.Velocity(50f, 0f), 320, 220, 1080, 2400)
        assertTrue(traj.isNotEmpty())
        assertTrue(traj.size <= FloatingPhysics.MAX_STEPS)
        assertTrue(traj.last().x > 100)
    }
    @Test fun fling_intoWallStopsAtMargin() {
        val traj = FloatingPhysics.simulateFling(FloatingPhysics.Position(100, 100), FloatingPhysics.Velocity(-60f, 0f), 320, 220, 1080, 2400)
        assertTrue(traj.isNotEmpty())
        assertEquals(FloatingKeyboard.MARGIN, traj.last().x)
    }
}
