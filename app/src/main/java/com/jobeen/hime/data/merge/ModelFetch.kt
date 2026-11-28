// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 模型下载/解包守卫（纯函数）：URL白名单、大小上限、zip路径防穿越；不联网不写文件。 */
object ModelFetch {
    val ALLOWED_HOSTS = listOf("github.com", "objects.githubusercontent.com", "raw.githubusercontent.com")
    fun urlAllowed(url: String): Boolean {
        if (!url.startsWith("https://")) return false
        val host = url.removePrefix("https://").substringBefore("/").substringBefore(":")
        return host in ALLOWED_HOSTS
    }
    fun sizeAllowed(bytes: Long): Boolean = bytes in 1..ModelLoader.MAX_MODEL_BYTES
    fun entrySafe(name: String): Boolean {
        if (name.isBlank()) return false
        val n = name.replace('\\', '/')
        return !n.contains("..") && !n.startsWith("/") && !n.contains("://")
    }
    fun destFor(entry: String): String? = if (entrySafe(entry)) "models/" + entry.trim('/') else null
}
