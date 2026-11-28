// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OcrPipelineTest {
    private fun grayOf(vararg vals: Int): ByteArray = ByteArray(vals.size) { vals[it].toByte() }

    @Test fun toGray_blackWhiteRed() {
        val black = OcrPipeline.toGray(intArrayOf(0xFF000000.toInt()))
        val white = OcrPipeline.toGray(intArrayOf(0xFFFFFFFF.toInt()))
        val red = OcrPipeline.toGray(intArrayOf(0xFFFF0000.toInt()))
        assertEquals(0, black[0].toInt() and 0xFF)
        assertEquals(255, white[0].toInt() and 0xFF)
        assertEquals(76, red[0].toInt() and 0xFF)
        assertTrue(OcrPipeline.toGray(intArrayOf()).isEmpty())
    }
    @Test fun otsu_uniformReturnsValue() {
        assertEquals(100, OcrPipeline.otsuThreshold(grayOf(100, 100, 100, 100)))
        assertEquals(128, OcrPipeline.otsuThreshold(ByteArray(0)))
    }
    @Test fun otsu_bimodalSeparates() {
        val data = ByteArray(100) { if (it < 50) 20.toByte() else 230.toByte() }
        val t = OcrPipeline.otsuThreshold(data)
        assertTrue("threshold $t", t >= 20 && t < 230)
    }
    @Test fun binarize_threshold() {
        val out = OcrPipeline.binarize(grayOf(0, 128, 255), 128)
        assertArrayEquals(byteArrayOf(0, 0, 255.toByte()), out)
    }
    @Test fun crop_clampsToBounds() {
        val gray = grayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val sub = OcrPipeline.cropGray(gray, 3, 3, 1, 1, 5, 5)
        assertArrayEquals(grayOf(5, 6, 8, 9), sub)
        assertEquals(2 to 2, OcrPipeline.croppedSize(3, 3, 1, 1, 5, 5))
        assertTrue(OcrPipeline.cropGray(gray, 3, 3, 5, 5, 2, 2).isEmpty())
    }
    @Test fun resize_half() {
        val gray = grayOf(1, 2, 3, 4)
        val small = OcrPipeline.resizeNearest(gray, 2, 2, 1, 1)
        assertEquals(1, small.size)
        assertEquals(1, small[0].toInt() and 0xFF)
    }
    @Test fun preprocess_validAndInvalid() {
        val argb = IntArray(4) { if (it < 2) 0xFF000000.toInt() else 0xFFFFFFFF.toInt() }
        val p = OcrPipeline.preprocess(argb, 2, 2)
        assertEquals(2, p!!.width); assertEquals(2, p.height); assertEquals(4, p.binary.size)
        assertNull(OcrPipeline.preprocess(argb, 0, 2))
        assertNull(OcrPipeline.preprocess(IntArray(1), 2, 2))
    }
}
