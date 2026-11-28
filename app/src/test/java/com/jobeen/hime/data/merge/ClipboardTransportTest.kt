// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ClipboardTransportTest {
    @Test fun debounce() { assertFalse(ClipboardTransport.shouldAccept(100, 0, "hi", "").accepted); assertTrue(ClipboardTransport.shouldAccept(900, 0, "hi", "").accepted) }
    @Test fun dup() { assertFalse(ClipboardTransport.shouldAccept(9999, 0, "hi", "hi").accepted) }
    @Test fun queueCap() { val q = ClipboardTransport.enqueue(List(32) { ClipboardTransport.Clip("id$it", "t", it.toLong()) }, ClipboardTransport.Clip("id32", "t", 99)); assertEquals(32, q.size) }
}
