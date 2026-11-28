// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ParallelEngineTest {
    @Test fun engine_roundtrip() {
        assertEquals("hi", ExternalEngine.decode(ExternalEngine.encode("hi")))
        assertEquals("", ExternalEngine.decode(byteArrayOf(1,2)))
    }
    @Test fun multilang_route() {
        assertEquals("pinyin", MultilangEngine.route("你好"))
        assertEquals("latin", MultilangEngine.route("hi"))
        assertEquals(MultilangEngine.DEFAULT_ENGINE, MultilangEngine.route("  "))
    }
}
