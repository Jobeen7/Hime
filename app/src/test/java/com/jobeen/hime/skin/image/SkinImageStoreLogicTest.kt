// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.skin.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SkinImageStore 纯逻辑（资产 id 校验、降采样、候选名）单测。
 * 只测 SkinImageStoreLogic，不触 Android 运行时。
 */
class SkinImageStoreLogicTest {

    @Test
    fun `safe asset ids are accepted`() {
        assertTrue(SkinImageStoreLogic.isSafeAssetId("scenic"))
        assertTrue(SkinImageStoreLogic.isSafeAssetId("key_face_1.png"))
        assertTrue(SkinImageStoreLogic.isSafeAssetId("a/b/c-1_2.webp"))
        assertTrue(SkinImageStoreLogic.isSafeAssetId("bg-2026.01.jpg"))
    }

    @Test
    fun `unsafe asset ids are rejected`() {
        assertFalse(SkinImageStoreLogic.isSafeAssetId(""))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("   "))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("/etc/passwd"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("../secret.png"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("a/../../b"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("has space.png"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("backslash\\x.png"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("semi;colon.png"))
        assertFalse(SkinImageStoreLogic.isSafeAssetId("x".repeat(250)))
    }

    @Test
    fun `candidate names append extensions when missing`() {
        val names = SkinImageStoreLogic.candidateNames("scenic")
        assertEquals(5, names.size)
        assertEquals("scenic.png", names[0])
        assertEquals("scenic.jpg", names[1])
        assertEquals(listOf("key.png"), SkinImageStoreLogic.candidateNames("key.png"))
        assertEquals(listOf("key.JPG"), SkinImageStoreLogic.candidateNames("key.JPG"))
        assertTrue(SkinImageStoreLogic.hasImageExtension("a.WEBP"))
        assertFalse(SkinImageStoreLogic.hasImageExtension("scenic"))
    }

    @Test
    fun `sample size is power of two and fits request`() {
        assertEquals(1, SkinImageStoreLogic.sampleSizeFor(100, 100, 1080, 480))
        assertEquals(2, SkinImageStoreLogic.sampleSizeFor(2000, 1000, 1000, 500))
        assertEquals(4, SkinImageStoreLogic.sampleSizeFor(4000, 2000, 1000, 500))
        // 无请求尺寸按最长边 1024 封顶
        assertEquals(4, SkinImageStoreLogic.sampleSizeFor(4096, 4096, 0, 0))
        assertEquals(1, SkinImageStoreLogic.sampleSizeFor(0, 0, 100, 100))
        assertEquals(1, SkinImageStoreLogic.sampleSizeFor(-5, 100, 100, 100))
        // 采样倍数恒为 2 的幂
        val s = SkinImageStoreLogic.sampleSizeFor(8000, 6000, 540, 240)
        assertTrue(s == 1 || (s and (s - 1)) == 0)
    }

    @Test
    fun `bitmap bytes estimate is width times height times 4`() {
        assertEquals(400, SkinImageStoreLogic.bitmapBytes(10, 10))
        assertEquals(0, SkinImageStoreLogic.bitmapBytes(0, 100))
        assertTrue(SkinImageStoreLogic.MAX_CACHE_BYTES > 0)
        assertTrue(SkinImageStoreLogic.MAX_DECODE_DIMENSION in 256..4096)
    }
}
