// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 外部引擎通道（纯函数）：消息编解码与握手参数。
 * 连接与进程创建由调用方承载，本模块只做长度前缀编解码
 * （UTF-8 文本，上限 8192 字节），编解码失败返回空而非抛异常；
 * 超时/重试均为常量，不在输入法热路径启动连接。
 */
object ExternalEngine {

    const val MAX_BYTES = 8192
    const val CONNECT_TIMEOUT_MS = 1500L
    const val MAX_RETRY = 2

    data class Handshake(val version: Int, val capabilities: List<String>)

    /** 编码：4 字节大端长度 + UTF-8 内容。 */
    fun encode(text: String): ByteArray {
        val body = text.toByteArray(Charsets.UTF_8).take(maxBytesBody(text))
        val out = ByteArray(4 + body.size)
        out[0] = (body.size ushr 24).toByte()
        out[1] = (body.size ushr 16).toByte()
        out[2] = (body.size ushr 8).toByte()
        out[3] = body.size.toByte()
        body.copyInto(out, 4)
        return out
    }

    /** 解码：校验长度前缀，非法返回空字符串。 */
    fun decode(bytes: ByteArray): String {
        if (bytes.size < 4) return ""
        val len = ((bytes[0].toInt() and 0xFF) shl 24) or
            ((bytes[1].toInt() and 0xFF) shl 16) or
            ((bytes[2].toInt() and 0xFF) shl 8) or
            (bytes[3].toInt() and 0xFF)
        if (len < 0 || len > MAX_BYTES || 4 + len > bytes.size) return ""
        return bytes.copyOfRange(4, 4 + len).toString(Charsets.UTF_8)
    }

    private fun maxBytesBody(text: String): Int =
        minOf(MAX_BYTES, text.toByteArray(Charsets.UTF_8).size)
}
