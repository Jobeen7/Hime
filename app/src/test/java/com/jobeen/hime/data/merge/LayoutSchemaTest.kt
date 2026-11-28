// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class LayoutSchemaTest {
    @Test fun flat() { val r = LayoutSchema.validate(mapOf("a" to "1", "b" to 2)); assertTrue(r.valid); assertEquals(2, r.flat.size) }
    @Test fun nested() { val r = LayoutSchema.validate(mapOf("g" to mapOf("a" to "1"))); assertTrue(r.valid) }
    @Test fun bad() { assertFalse(LayoutSchema.validate(mapOf("a" to " ")).valid); assertFalse(LayoutSchema.validate(emptyMap()).valid) }
}
