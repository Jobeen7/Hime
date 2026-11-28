// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class RenderBindTest {
    @Test fun clamped() { val p = RenderBind.bind(FloatingWindowView.WindowState(x = 99999, y = 99999), 1080, 2400); assertTrue(p.x in FloatingKeyboard.MARGIN..1080) }
    @Test fun lockedPassthroughFlags() { val p = RenderBind.bind(FloatingWindowView.WindowState(locked = true, passthrough = true), 1080, 2400); assertEquals(RenderBind.TOUCH_LOCKED or RenderBind.TOUCH_PASSTHROUGH, p.touchMode) }
    @Test fun draggableDefault() { assertEquals(0, RenderBind.bind(FloatingWindowView.WindowState(), 1080, 2400).touchMode) }
    @Test fun visible() { assertTrue(RenderBind.visible(RenderBind.bind(FloatingWindowView.WindowState(), 1080, 2400))) }
}
