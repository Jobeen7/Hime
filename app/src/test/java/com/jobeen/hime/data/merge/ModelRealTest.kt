// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ModelRealTest {
    @Test fun registerAndLoad() {
        val dir = java.io.File.createTempDir("model-real")
        try {
            ModelReal.clear()
            val f = java.io.File(dir, "hw.bin")
            f.writeBytes(ByteArray(100) { 5 })
            assertTrue(ModelReal.registerFile(dir, ModelLoader.Kind.HANDWRITING, "hw.bin", 1))
            val loaded = ModelReal.loadFile(dir, "hw.bin")!!
            assertEquals(100, loaded.bytes.size)
            assertNull(ModelReal.loadFile(dir, "../escape"))
            ModelReal.clear()
        } finally { dir.deleteRecursively() }
    }
    @Test fun preprocess() { assertEquals(listOf(0, 2, 4), ModelReal.preprocess(listOf(10, 14, 18), 10, 2)) }
    @Test fun box() { val (w, h) = ModelReal.targetBox(200, 100); assertEquals(96, w); assertEquals(48, h) }
}
