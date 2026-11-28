// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class HandwriteOcrImplTest {
    @Test fun strokeSplit() {
        val p = listOf(Handwriting.Point(0,0), Handwriting.Point(1,1), null, Handwriting.Point(2,2), Handwriting.Point(3,3))
        assertEquals(2, HandwritingStroke.split(p).size)
        assertEquals(0, HandwritingStroke.split(listOf(Handwriting.Point(0,0))).size)
    }
    @Test fun strokeGrid() {
        val g = HandwritingStroke.grid(listOf(Handwriting.Point(0,0), Handwriting.Point(10,10)))
        assertEquals(2, g.size)
        assertTrue(g.all { it in 0..63 })
    }
    @Test fun ocrPageMergeDedup() {
        assertEquals(listOf("a","b","c"), OcrPage.merge(listOf(listOf("a","b"), listOf("b","c"))))
        assertEquals("", OcrPage.pageText(emptyList()))
    }
}
