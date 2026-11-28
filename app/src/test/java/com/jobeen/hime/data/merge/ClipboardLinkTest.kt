// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ClipboardLinkTest {
    @Test fun freshIngested() { assertTrue(ClipboardLink.shouldIngest("hi", null)) }
    @Test fun duplicateSkipped() { assertFalse(ClipboardLink.shouldIngest("hi", "hi")) }
    @Test fun blankSkipped() { assertFalse(ClipboardLink.shouldIngest("  ", null)) }
    @Test fun oversizeSkipped() { assertFalse(ClipboardLink.shouldIngest("x".repeat(2500), null)) }
    @Test fun entry() { assertNotNull(ClipboardLink.toEntry("a", "hi", 1)); assertNull(ClipboardLink.toEntry(" ", "hi", 1)) }
    @Test fun dedup() { assertEquals(listOf("a", "b"), ClipboardLink.dedup(listOf("a", "a", "b"))) }
}
