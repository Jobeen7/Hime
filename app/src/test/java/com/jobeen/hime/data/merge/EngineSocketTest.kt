// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class EngineSocketTest {
    private class Loopback(var data: ByteArray = ByteArray(0), var pos: Int = 0) : EngineSocket.Connection {
        val written = mutableListOf<ByteArray>()
        var closed = false
        override fun write(bytes: ByteArray) { written.add(bytes.copyOf()); data = ExternalEngine.encode("pong"); pos = 0 }
        override fun readExactly(n: Int): ByteArray? { if (pos + n > data.size) return null; val out = data.copyOfRange(pos, pos + n); pos += n; return out }
        override fun close() { closed = true }
        override fun isAlive(): Boolean = !closed
    }
    @Test fun roundtrip() {
        val conn = Loopback()
        val client = EngineSocket.Client(object : EngineSocket.Connector { override fun connect(h: String, p: Int) = conn })
        assertTrue(client.connect("127.0.0.1", 9999))
        assertEquals("pong", client.send("ping"))
        assertTrue(client.ping())
        client.close()
        assertTrue(conn.closed)
    }
    @Test fun badHostRejected() {
        val client = EngineSocket.Client(object : EngineSocket.Connector { override fun connect(h: String, p: Int): EngineSocket.Connection? = null })
        assertFalse(client.connect("", 80))
        assertFalse(client.connect("host", 80))
    }
}
