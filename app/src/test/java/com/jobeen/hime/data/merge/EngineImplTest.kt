// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class EngineImplTest {
    @Test fun handshakeReady() {
        val s = ExternalSession.start(ExternalSession.Session())
        val ok = ExternalSession.handshakeOk(s, ExternalEngine.Handshake(2, listOf("query")))
        assertEquals(ExternalSession.Phase.READY, ok.phase)
        assertTrue(ExternalSession.encodeChecked(ok, "hello").isNotEmpty())
    }
    @Test fun handshakeBadFails() {
        val s = ExternalSession.start(ExternalSession.Session())
        val bad = ExternalSession.handshakeOk(s, ExternalEngine.Handshake(0, emptyList()))
        assertEquals(ExternalSession.Phase.FAILED, bad.phase)
    }
    @Test fun retryLimit() {
        val s = ExternalSession.Session(attempts = ExternalEngine.MAX_RETRY)
        assertFalse(ExternalSession.canRetry(s))
    }
    @Test fun notReadyNoEncode() {
        assertEquals(0, ExternalSession.encodeChecked(ExternalSession.Session(), "x").size)
    }
    @Test fun multilangFallback() {
        assertEquals(MultilangEngine.DEFAULT_ENGINE, MultilangEngine.route("   "))
        assertEquals("kana", MultilangEngine.route("こんにちは"))
    }
}
