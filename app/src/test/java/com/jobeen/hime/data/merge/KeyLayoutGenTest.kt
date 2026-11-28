// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class KeyLayoutGenTest {
    @Test fun generate_26_eachSingle() {
        val g = KeyLayoutGen.generate(26, "abcdefghijklmnopqrstuvwxyz")
        assertEquals(26, g.size)
        assertTrue(g.all { it.length == 1 })
        assertTrue(KeyLayoutGen.isValidPartition(g, "abcdefghijklmnopqrstuvwxyz"))
    }
    @Test fun generate_18_countAndPartition() {
        val g = KeyLayoutGen.generate(18, "abcdefghijklmnopqrstuvwxyz")
        assertEquals(18, g.size)
        assertTrue(KeyLayoutGen.isValidPartition(g, "abcdefghijklmnopqrstuvwxyz"))
    }
    @Test fun generate_9_14_valid() {
        assertTrue(KeyLayoutGen.isValidPartition(KeyLayoutGen.generate(9, "abcdefghijklmnopqrstuvwxyz"), "abcdefghijklmnopqrstuvwxyz"))
        assertEquals(9, KeyLayoutGen.generate(9, "abcdefghijklmnopqrstuvwxyz").size)
        assertEquals(14, KeyLayoutGen.generate(14, "abcdefghijklmnopqrstuvwxyz").size)
        assertTrue(KeyLayoutGen.isValidPartition(KeyLayoutGen.generate(14, "abcdefghijklmnopqrstuvwxyz"), "abcdefghijklmnopqrstuvwxyz"))
    }
    @Test fun generate_illegalCount_empty() {
        assertTrue(KeyLayoutGen.generate(10, "abcdefghijklmnopqrstuvwxyz").isEmpty())
        assertTrue(KeyLayoutGen.generate(0, "abcdefghijklmnopqrstuvwxyz").isEmpty())
    }
    @Test fun partition_duplicateOrMissing_false() {
        assertFalse(KeyLayoutGen.isValidPartition(listOf("ab", "ab"), "abcdefghijklmnopqrstuvwxyz"))
        assertFalse(KeyLayoutGen.isValidPartition(listOf("abc"), "abcdefghijklmnopqrstuvwxyz"))
    }
    @Test fun splitAndMerge_preserveLetters() {
        val g18 = KeyLayoutGen.generate(18, "abcdefghijklmnopqrstuvwxyz")
        val split = KeyLayoutGen.splitGroup(g18, 17, 1)
        assertNotNull(split)
        assertEquals(19, split!!.size)
        assertTrue(KeyLayoutGen.isValidPartition(split, "abcdefghijklmnopqrstuvwxyz"))
        val merged = KeyLayoutGen.mergeGroups(g18, 0, 1)
        assertNotNull(merged)
        assertEquals(17, merged!!.size)
        assertTrue(KeyLayoutGen.isValidPartition(merged, "abcdefghijklmnopqrstuvwxyz"))
    }
    @Test fun split_invalid_null() {
        val g = KeyLayoutGen.generate(26, "abcdefghijklmnopqrstuvwxyz")
        assertNull(KeyLayoutGen.splitGroup(g, 0, 0))
        assertNull(KeyLayoutGen.mergeGroups(g, 1, 1))
    }
    @Test fun colemak_spotChecks() {
        assertEquals('f', KeyLayoutGen.colemak('e'))
        assertEquals('s', KeyLayoutGen.colemak('r'))
        assertEquals('a', KeyLayoutGen.colemak('a'))
        assertEquals('k', KeyLayoutGen.colemak('n'))
    }
    @Test fun hexRows_nonEmpty() {
        assertTrue(KeyLayoutGen.hexRows().isNotEmpty())
    }
}
