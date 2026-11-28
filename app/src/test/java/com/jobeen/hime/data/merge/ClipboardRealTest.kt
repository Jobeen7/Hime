// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
class ClipboardRealTest {
    @Test fun mergeNewerWins() {
        val m = ClipboardReal.mergeByTime(listOf(ClipboardReal.Timed("a", "old", 1L)), listOf(ClipboardReal.Timed("a", "new", 2L)))
        assertEquals("new", m.single().text)
    }
    @Test fun serializeParse() {
        val items = listOf(ClipboardReal.Timed("1", "hi", 100L), ClipboardReal.Timed("2", "there", 200L))
        assertEquals(items, ClipboardReal.parse(ClipboardReal.serialize(items)))
    }
    @Test fun persistentRoundtrip() {
        val dir = Files.createTempDirectory("clip-real").toFile()
        try {
            val items = listOf(ClipboardReal.Timed("x", "hello", 500L))
            assertTrue(ClipboardReal.save(dir, items))
            assertEquals(items, ClipboardReal.load(dir))
        } finally { dir.deleteRecursively() }
    }
    @Test fun rejectsBlank() { assertTrue(ClipboardReal.mergeByTime(listOf(ClipboardReal.Timed(" ", "x", 1L)), emptyList()).isEmpty()) }
}
