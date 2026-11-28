// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * 外部引擎真实 Socket 单帧通道（EngineLink，补充 EngineSocket.Client）：长度前缀帧（复用 ExternalEngine 编解码），
 * 建连 1500ms 超时、读写超时与单帧上限 8192；传输失败返回 null/空帧。
 * Socket 工厂可注入，便于单测用 loopback 回环验证真实往返。
 */
object EngineLink {
    const val TIMEOUT_MS = 1500

    interface SocketFactory {
        fun open(host: String, port: Int, timeoutMs: Int): Socket
    }

    object RealSocketFactory : SocketFactory {
        override fun open(host: String, port: Int, timeoutMs: Int): Socket {
            val s = Socket()
            s.connect(InetSocketAddress(host, port), timeoutMs)
            s.soTimeout = timeoutMs
            return s
        }
    }

    fun validTarget(host: String, port: Int): Boolean {
        if (host.isBlank() || host.contains(' ') || host.contains('\n') || host.contains('\r')) return false
        return port in 1024..65535
    }

    /** 发送一帧并读一帧应答；任何异常返回 null。 */
    fun request(factory: SocketFactory, host: String, port: Int, text: String): String? {
        if (!validTarget(host, port) || text.isBlank()) return null
        var socket: Socket? = null
        return try {
            socket = factory.open(host, port, TIMEOUT_MS)
            val out: OutputStream = socket.getOutputStream()
            out.write(ExternalEngine.encode(text))
            out.flush()
            readFrame(socket.getInputStream())
        } catch (e: Exception) {
            null
        } finally {
            try {
                socket?.close()
            } catch (e: Exception) {
            }
        }
    }

    /** 读一帧：先读 4 字节头，再补足 len 字节；流提前结束返回 null。 */
    fun readFrame(input: InputStream): String? {
        val stream = if (input is BufferedInputStream) input else BufferedInputStream(input)
        val head = ByteArray(4)
        if (!readFully(stream, head, 0, 4)) return null
        val len = ((head[0].toInt() and 0xFF) shl 24) or
            ((head[1].toInt() and 0xFF) shl 16) or
            ((head[2].toInt() and 0xFF) shl 8) or
            (head[3].toInt() and 0xFF)
        if (len < 0 || len > ExternalEngine.MAX_BYTES) return null
        val body = ByteArray(len)
        if (len > 0 && !readFully(stream, body, 0, len)) return null
        return body.toString(Charsets.UTF_8)
    }

    private fun readFully(stream: InputStream, buf: ByteArray, off: Int, len: Int): Boolean {
        var read = 0
        while (read < len) {
            val n = stream.read(buf, off + read, len - read)
            if (n == -1) return false
            read += n
        }
        return true
    }
}
