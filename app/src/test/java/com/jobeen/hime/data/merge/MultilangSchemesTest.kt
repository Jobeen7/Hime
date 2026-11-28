// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class MultilangSchemesTest {
    @Test fun catalogCoversRequired() {
        for (id in listOf("ja-jaroomaji", "ko-hangul", "wubi86", "wubi98", "cangjie", "zhuyin", "stroke", "western", "fr", "de", "ru", "vi")) {
            assertNotNull("missing $id", MultilangSchemes.find(id))
        }
        assertEquals("kana", MultilangSchemes.layoutHint("ja-jaroomaji"))
        assertEquals("zhuyin", MultilangSchemes.layoutHint("zhuyin"))
        assertNull(MultilangSchemes.find("nope"))
    }
    @Test fun transactionalRollback() {
        val base = setOf("wubi86")
        assertNull(MultilangSchemes.transactionalInstall(base, "ja-jaroomaji", verifyOk = false))
        assertEquals(setOf("wubi86"), base)
        assertEquals(setOf("wubi86", "ja-jaroomaji"), MultilangSchemes.transactionalInstall(base, "ja-jaroomaji", verifyOk = true))
        assertEquals(setOf("wubi86"), MultilangSchemes.uninstall(setOf("wubi86", "ja-jaroomaji"), "ja-jaroomaji"))
    }
}
