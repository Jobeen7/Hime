// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class LayoutFullTest {
    @Test fun defaultOk() { assertTrue(LayoutFull.validateFull(LayoutFull.default()).ok) }
    @Test fun badVersion() { val m = LayoutFull.default() + ("schemaVersion" to 99); assertFalse(LayoutFull.validateFull(m).ok) }
    @Test fun badHeight() { val m = LayoutFull.default() + ("height" to 9999); val v = LayoutFull.validateFull(m); assertTrue(v.errors.contains("bad_height")) }
    @Test fun badTheme() { val m = LayoutFull.default() + ("theme" to "neon"); assertFalse(LayoutFull.validateFull(m).ok) }
}
