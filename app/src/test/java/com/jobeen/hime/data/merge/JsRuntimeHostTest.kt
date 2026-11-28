// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class JsRuntimeHostTest {
    private val good = JsRuntimeHost.Manifest("tb", "1.0", listOf("example.com", "api.test.org"), 2000)
    @Test fun validateRejectWildcardAndTimeout() {
        assertNull(JsRuntimeHost.validate(good))
        assertEquals("domain wildcard denied", JsRuntimeHost.validate(good.copy(allowedDomains = listOf("*.example.com"))))
        assertEquals("timeout", JsRuntimeHost.validate(good.copy(timeoutMs = 10)))
        assertEquals("domain format", JsRuntimeHost.validate(good.copy(allowedDomains = listOf("no-tld"))))
    }
    @Test fun domainDenyByDefault() {
        assertTrue(JsRuntimeHost.canRequest(good, "example.com"))
        assertTrue(JsRuntimeHost.canRequest(good, "EXAMPLE.com"))
        assertFalse(JsRuntimeHost.canRequest(good, "evil.com"))
        assertFalse(JsRuntimeHost.canRequest(good.copy(enabled = false), "example.com"))
    }
    @Test fun undoAndBudget() {
        val u = JsRuntimeHost.UndoStack()
        assertEquals("new", u.apply("old", "new"))
        assertEquals("old", u.undo("new"))
        assertNull(u.undo("new"))
        assertEquals(JsRuntimeHost.Code.TOO_MANY, JsRuntimeHost.runWithBudget(10, 5).code)
        assertEquals(JsRuntimeHost.Code.OK, JsRuntimeHost.runWithBudget(3, 5).code)
    }
}
