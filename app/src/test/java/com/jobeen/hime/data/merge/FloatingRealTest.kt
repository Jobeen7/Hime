// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class FloatingRealTest {
    @Test fun velocity() { val v = FloatingReal.velocity(100, -50, 100L); assertEquals(1000f, v.vx, 0.01f); assertEquals(-500f, v.vy, 0.01f) }
    @Test fun snapLeft() { val s = FloatingWindowView.WindowState(x = 10, y = 500, w = 320, h = 220); assertEquals(FloatingKeyboard.MARGIN, FloatingReal.snap(s, 1080, 2400).x) }
    @Test fun snapCenterKept() { val s = FloatingWindowView.WindowState(x = 380, y = 1000, w = 320, h = 220); assertEquals(380, FloatingReal.snap(s, 1080, 2400).x) }
    @Test fun lockedDragIgnored() { val s = FloatingWindowView.WindowState(x = 100, y = 200, locked = true); assertEquals(100, FloatingReal.dragResult(s, 500, 500).x) }
    @Test fun passthrough() { assertEquals(1, FloatingReal.passthroughDepth(true)); assertEquals(0, FloatingReal.passthroughDepth(false)) }
}
