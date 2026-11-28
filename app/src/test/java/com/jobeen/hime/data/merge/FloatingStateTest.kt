// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class FloatingStateTest {
    @Test fun showVisible() { val s = FloatingState.show(1080, 2400, 600); assertTrue(s.visible); assertTrue(s.frame.width > 0) }
    @Test fun moveClamped() { val s = FloatingState.show(1080, 2400, 600); val m = FloatingState.move(s, -99999, 0, 1080, 2400); assertEquals(FloatingKeyboard.MARGIN, m.frame.x) }
    @Test fun hideKeepsFrame() { val s = FloatingState.show(1080, 2400, 600); val h = FloatingState.hide(s); assertFalse(h.visible); assertEquals(s.frame, h.frame) }
    @Test fun resizeClamped() { val s = FloatingState.show(1080, 2400, 600); assertTrue(FloatingState.resize(s, 5f, 1080, 600).scale <= FloatingKeyboard.SCALE_LARGE) }
    @Test fun snapLeft() { val s = FloatingState.show(1080, 2400, 600).copy(frame = FloatingKeyboard.Frame(10, 100, 300, 200)); assertEquals(FloatingKeyboard.MARGIN, FloatingState.snap(s, 1080).frame.x) }
}
