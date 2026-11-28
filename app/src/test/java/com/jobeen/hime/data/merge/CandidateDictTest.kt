// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class CandidateDictTest {
    @Test fun detailUnicode() {
        val d = CandidateDict.detailFor("汉", listOf("han4"), "氵", 5, "icwg")!!
        assertEquals("U+6C49", d.unicodeHex)
        assertEquals(listOf("han4"), d.readings)
        assertNull(CandidateDict.detailFor("  "))
        assertTrue(CandidateDict.emojiCollapsed(true))
        assertFalse(CandidateDict.emojiCollapsed(false))
    }
    @Test fun parseAndRollback() {
        val ok = CandidateDict.parse("你好 100\n世界 50", CandidateDict.Source.TXT)!!
        assertEquals(2, ok.size)
        assertEquals(1, CandidateDict.parse("你好", CandidateDict.Source.QQ)!![0].weight)
        assertNull(CandidateDict.parse("你好 100\n坏行 abc", CandidateDict.Source.TXT))
        assertNull(CandidateDict.parse("你好 100\n词三个字多 10 20", CandidateDict.Source.SOGOU))
    }
    @Test fun mergeDedupMax() {
        val merged = CandidateDict.importTransactional(listOf(CandidateDict.Entry("你好", 10)), listOf(CandidateDict.Entry("你好", 99), CandidateDict.Entry("世界", 5)))
        assertEquals(2, merged.size)
        assertEquals(99, merged.first { it.word == "你好" }.weight)
    }
}
