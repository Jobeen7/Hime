// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class FloatingWindowViewTest {
    @Test fun moveClamps() { val s = FloatingWindowView.move(FloatingWindowView.WindowState(x=8,y=8), -100, 5000); assertTrue(s.x >= 8); assertTrue(s.y >= 8) }
    @Test fun lockedNoMove() { val s = FloatingWindowView.toggleLock(FloatingWindowView.WindowState(x=11,y=12)); assertEquals(s, FloatingWindowView.move(s, 99, 99)) }
    @Test fun alphaBounds() { assertEquals(0.35f, FloatingWindowView.setAlpha(FloatingWindowView.WindowState(), 0.1f).alpha, 0f); assertEquals(1f, FloatingWindowView.setAlpha(FloatingWindowView.WindowState(), 2f).alpha, 0f) }
}
