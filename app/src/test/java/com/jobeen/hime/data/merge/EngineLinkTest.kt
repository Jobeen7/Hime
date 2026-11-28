// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.io.ByteArrayInputStream
import org.junit.Assert.*
import org.junit.Test

class EngineLinkTest {
    @Test fun validTarget() {
        assertTrue(EngineLink.validTarget("127.0.0.1", 9000))
        assertFalse(EngineLink.validTarget("", 9000))
        assertFalse(EngineLink.validTarget("127.0.0.1", 80))
        assertFalse(EngineLink.validTarget("bad host", 9000))
    }

    @Test fun readFrameRoundtrip() {
        val frame = ExternalEngine.encode("ping")
        assertEquals("ping", EngineLink.readFrame(ByteArrayInputStream(frame)))
    }

    @Test fun readFrameTruncatedNull() {
        assertNull(EngineLink.readFrame(ByteArrayInputStream(byteArrayOf(0, 0))))
        val frame = ExternalEngine.encode("hello")
        assertNull(EngineLink.readFrame(ByteArrayInputStream(frame.copyOf(frame.size - 2))))
    }

    @Test fun requestBlankNull() {
        assertNull(EngineLink.request(EngineLink.RealSocketFactory, "127.0.0.1", 9999, " "))
    }

    @Test fun loopbackEcho() {
        val server = java.net.ServerSocket(0)
        server.soTimeout = 3000
        val port = server.localPort
        val t = Thread {
            server.use { s ->
                val client = s.accept()
                client.soTimeout = 3000
                val req = EngineLink.readFrame(client.getInputStream())
                client.getOutputStream().write(ExternalEngine.encode("echo:" + req))
                client.getOutputStream().flush()
                client.close()
            }
        }
        t.start()
        assertEquals("echo:ping", EngineLink.request(EngineLink.RealSocketFactory, "127.0.0.1", port, "ping"))
        t.join(3000)
    }
}
