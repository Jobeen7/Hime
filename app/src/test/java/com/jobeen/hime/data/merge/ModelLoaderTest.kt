// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ModelLoaderTest {
    @org.junit.Before fun reset() = ModelLoader.clear()
    @Test fun register() { assertTrue(ModelLoader.register(ModelLoader.Descriptor(ModelLoader.Kind.OCR, "ocr-v1", 100, 1))); assertEquals("ocr-v1", ModelLoader.get("ocr-v1")?.name) }
    @Test fun badSize() { assertFalse(ModelLoader.register(ModelLoader.Descriptor(ModelLoader.Kind.OCR, "big", ModelLoader.MAX_MODEL_BYTES + 1L, 1))) }
    @Test fun normalize() { assertEquals(listOf(1, 2, 3), ModelLoader.normalize(listOf(11, 12, 13), 10, 1)) }
    @Test fun box() { assertEquals(224 to 112, ModelLoader.imageBox(640, 320, 224)) }
}
