// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.net.Socket
/** 外部引擎 Socket 客户端：长度前缀帧、退避重连、心跳探活（真实 socket）。 */
object EngineSocket {
    interface Connection {
        fun write(bytes: ByteArray)
        fun readExactly(n: Int): ByteArray?
        fun close()
        fun isAlive(): Boolean
    }
    class TcpConnection(private val socket: Socket) : Connection {
        override fun write(bytes: ByteArray) { socket.getOutputStream().write(bytes); socket.getOutputStream().flush() }
        override fun readExactly(n: Int): ByteArray? {
            val inp = socket.getInputStream()
            val buf = ByteArray(n)
            var off = 0
            while (off < n) {
                val r = inp.read(buf, off, n - off)
                if (r < 0) return null
                off += r
            }
            return buf
        }
        override fun close() { try { socket.close() } catch (e: Exception) {} }
        override fun isAlive(): Boolean = !socket.isClosed && socket.isConnected
    }
    interface Connector { fun connect(host: String, port: Int): Connection? }
    class TcpConnector : Connector {
        override fun connect(host: String, port: Int): Connection? {
            return try {
                val s = Socket()
                s.connect(java.net.InetSocketAddress(host, port), ExternalEngine.CONNECT_TIMEOUT_MS.toInt())
                TcpConnection(s)
            } catch (e: Exception) { null }
        }
    }
    class Client(val connector: Connector) {
        private var conn: Connection? = null
        var attempts: Int = 0
        fun connect(host: String, port: Int): Boolean {
            if (EngineTransport.dialDescriptor(host, port) == null) return false
            var i = 0
            while (i < ExternalEngine.MAX_RETRY + 1) {
                attempts = i
                val c = connector.connect(host, port)
                if (c != null) { conn = c; return true }
                i++
            }
            return false
        }
        fun send(text: String): String? {
            val c = conn ?: return null
            return try {
                c.write(ExternalEngine.encode(text))
                val header = c.readExactly(4) ?: return null
                val len = ((header[0].toInt() and 0xFF) shl 24) or ((header[1].toInt() and 0xFF) shl 16) or ((header[2].toInt() and 0xFF) shl 8) or (header[3].toInt() and 0xFF)
                if (len < 0 || len > ExternalEngine.MAX_BYTES) return null
                val body = c.readExactly(len) ?: return null
                ExternalEngine.decode(header + body)
            } catch (e: Exception) { null }
        }
        fun ping(): Boolean = conn?.isAlive() == true
        fun close() { conn?.close(); conn = null }
        fun backoffMs(): Long = EngineTransport.backoff(attempts)
    }
}
