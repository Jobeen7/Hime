// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutEditorModelTest {
    @Test fun reorder_preservesIds() {
        val ids = listOf("a", "b", "c", "d")
        val out = LayoutEditorModel.reorder(ids, listOf(Pair(0, 3)))
        assertEquals(listOf("b", "c", "d", "a"), out)
    }
    @Test fun reorder_duplicate_rejected() {
        assertNull(LayoutEditorModel.reorder(listOf("a", "a"), emptyList()))
        assertFalse(LayoutEditorModel.validateIds(listOf("a", "")))
    }
    @Test fun pair_autocompleteAndBackspace() {
        val s = PairSymbol.applyPair("", 0, '(')
        assertEquals("()", s)
        assertTrue(PairSymbol.shouldDeletePair("()", 1))
        assertFalse(PairSymbol.shouldDeletePair("(x)", 1))
    }
    @Test fun pair_shouldAutocomplete_nextCloser_no() {
        assertFalse(PairSymbol.shouldAutocomplete('(', ')'))
        assertTrue(PairSymbol.shouldAutocomplete('(', null))
    }
    @Test fun autoSpace_mixedOnly() {
        assertTrue(PairSymbol.needsAutoSpace('a', '测', true))
        assertFalse(PairSymbol.needsAutoSpace('a', 'b', true))
        assertFalse(PairSymbol.needsAutoSpace('a', '测', false))
    }
    @Test fun pinAndPortGuard() {
        assertTrue(WebEditorGuard.checkPin("1234", "1234"))
        assertFalse(WebEditorGuard.checkPin("1234", "9999"))
        assertFalse(WebEditorGuard.isValidPin("abc"))
        val g = WebEditorGuard.PortGuard()
        assertTrue(g.acquire(8080))
        assertFalse(g.acquire(8081))
        assertTrue(g.release())
        assertTrue(g.acquire(8081))
    }
}
