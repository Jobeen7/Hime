// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutImportTest {

    @Test
    fun parseYaml_simple() {
        val keys = LayoutImport.parseYaml("A: a\nB: b\n")
        assertEquals(2, keys.size)
        assertEquals("A", keys[0].label)
        assertEquals("a", keys[0].value)
    }

    @Test
    fun parseYaml_skipsCommentsAndBadLines() {
        val keys = LayoutImport.parseYaml("# comment\nbadline\nC: c\n")
        assertEquals(1, keys.size)
        assertEquals("c", keys[0].value)
    }

    @Test
    fun parseYaml_invalidLine_noException() {
        val keys = LayoutImport.parseYaml(":::\n: v\nk:\n")
        assertTrue(keys.isEmpty())
    }

    @Test
    fun parseIni_groupsBySection() {
        val keys = LayoutImport.parseIni("[row1]\nA=a\nB=b\n")
        assertEquals(2, keys.size)
        assertEquals("row1:A", keys[0].label)
    }

    @Test
    fun parseIni_noSection_plainLabel() {
        val keys = LayoutImport.parseIni("A=a\n")
        assertEquals("A", keys[0].label)
    }

    @Test
    fun parseYaml_respectsMaxKeys() {
        val text = (1..100).joinToString("\n") { "K$it: v$it" }
        assertEquals(LayoutImport.MAX_KEYS, LayoutImport.parseYaml(text).size)
    }
}
