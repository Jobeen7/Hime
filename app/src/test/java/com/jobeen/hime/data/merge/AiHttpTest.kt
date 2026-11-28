// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class AiHttpTest {
    private class Ok : AiHttp.Client { override fun send(r: AiHttp.Request) = AiHttp.Response(true, " ok ", 200) }
    private class Flaky(var n: Int = 0) : AiHttp.Client { override fun send(r: AiHttp.Request) = if (++n < 2) AiHttp.Response(false, "", 503) else AiHttp.Response(true, "ok", 200) }
    @Test fun validate() { assertNotNull(AiHttp.validate(AiHttp.Request("http://x", "hi", 64))); assertNull(AiHttp.validate(AiHttp.Request("https://x", "hi", 64))) }
    @Test fun retry() { assertTrue(AiHttp.withRetry(Flaky(), AiHttp.Request("https://x", "hi", 64)).ok) }
    @Test fun ok() { assertTrue(AiHttp.withRetry(Ok(), AiHttp.Request("https://x", "hi", 64)).ok) }
}
