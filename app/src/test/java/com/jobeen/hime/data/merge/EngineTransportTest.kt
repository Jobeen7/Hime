// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class EngineTransportTest {
    @Test fun backoff() { assertEquals(1500L, EngineTransport.backoff(0)); assertEquals(3000L, EngineTransport.backoff(1)) }
    @Test fun budget() { assertTrue(EngineTransport.canReconnect(EngineTransport.Health(true, 1, 0))); assertFalse(EngineTransport.canReconnect(EngineTransport.Health(true, 2, 0))) }
    @Test fun dial() { assertEquals("engine:127.0.0.1:18080", EngineTransport.dialDescriptor("127.0.0.1", 18080)); assertNull(EngineTransport.dialDescriptor("", 18080)); assertNull(EngineTransport.dialDescriptor("h", 80)) }
}
