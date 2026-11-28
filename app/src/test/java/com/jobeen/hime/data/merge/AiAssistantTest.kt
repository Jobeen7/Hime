// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class AiAssistantTest {
    @Test fun buildRejectBlankAndLong() {
        assertNull(AiAssistant.buildRequest(AiAssistant.Provider.AI_GENERIC, AiAssistant.Op.REWRITE, "zh", "  ", 1L))
        assertNull(AiAssistant.buildRequest(AiAssistant.Provider.TENCENT, AiAssistant.Op.TRANSLATE, "", "hi", 1L))
        assertNotNull(AiAssistant.buildRequest(AiAssistant.Provider.YOUDAO, AiAssistant.Op.TRANSLATE, "EN", "hi", 1L))
    }
    @Test fun previewCommitTokenMismatchBlocked() {
        val p = AiAssistant.makePreview(" hello ", 7L)!!
        assertEquals("hello", p.text)
        assertNull(AiAssistant.commit(p, 8L))
        assertEquals("hello", AiAssistant.commit(p, 7L))
    }
    @Test fun cacheIsolated() {
        val a = AiAssistant.cacheKey(AiAssistant.Provider.MICROSOFT, AiAssistant.Op.TRANSLATE, "en", "hi")
        val b = AiAssistant.cacheKey(AiAssistant.Provider.TENCENT, AiAssistant.Op.TRANSLATE, "en", "hi")
        assertNotEquals(a, b)
    }
    @Test fun streamStopsOnDisconnect() {
        val client = object : AiAssistant.StreamClient {
            override fun lines(req: AiAssistant.Request): List<String> = listOf("a", "b", "DISCONNECT cut", "c")
        }
        val req = AiAssistant.buildRequest(AiAssistant.Provider.VOLC, AiAssistant.Op.REWRITE, "zh", "t", 1L)!!
        assertEquals(listOf("a", "b"), AiAssistant.stream(client, req))
    }
}
