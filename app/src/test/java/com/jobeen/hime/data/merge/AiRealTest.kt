// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class AiRealTest {
    @Test fun httpRejectedLocally() { val c = AiRealClient.UrlClient(); val r = c.send(AiHttp.Request("http://insecure", "hi", 64)); assertFalse(r.ok); assertEquals(0, r.code) }
    @Test fun budgetAdd() { assertEquals(8064L, AiRealClient.budgetMs(64, 8000L)) }
    @Test fun parseTrims() { assertEquals("ok", AiHttp.parseReply("ok")) }
}
