// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.clipboard.sync

/**
 * F13 剪贴板同步协议（纯函数）：跨设备同步的过滤与差分。
 * 复用现有 ClipboardSync.diff 与 ClipboardChunker.chunk 的口径，
 * 本文件补：条目类型/来源、过滤规则（最大体积、后缀、长度）、
 * 验证码自动提取、分类 Tab。真实传输（OkHttp WebSocket/HTTP）由调用方
 * 在用户开启同步后接入；本模块不持连接、不常驻。
 */
enum class ClipKind { TEXT, IMAGE, FILE, LINK }
enum class ClipTab { LOCAL, OTHER_DEVICE, IMAGE_FILE, ALL }

data class ClipSyncItem(
    val id: String,
    val kind: ClipKind,
    val text: String = "",
    val localUri: String = "",
    val sourceDevice: String = "",
    val version: Int = 0,
)

object ClipSyncProtocol {
    const val MAX_BYTES = 8 * 1024 * 1024
    const val MAX_TEXT_LEN = 2000
    private val DENIED_SUFFIX = setOf("exe", "apk", "bat", "sh", "so", "dex")
    private val CODE_REGEX = Regex("\\b(\\d{4,6})\\b")

    /** 过滤：超长文本截断保留；被拒后缀返回 null；空白 id 返回 null。 */
    fun filter(item: ClipSyncItem): ClipSyncItem? {
        if (item.id.isBlank()) return null
        if (item.kind == ClipKind.FILE && item.localUri.isNotBlank()) {
            val suffix = item.localUri.substringAfterLast('.', "").lowercase()
            if (suffix in DENIED_SUFFIX) return null
        }
        val clipped = if (item.text.length > MAX_TEXT_LEN) item.text.take(MAX_TEXT_LEN) else item.text
        return item.copy(text = clipped)
    }

    /** 短信验证码提取：取首个 4-6 位数字；无则 null。 */
    fun extractCode(text: String): String? = CODE_REGEX.find(text)?.value

    fun tabOf(item: ClipSyncItem, isLocal: Boolean): ClipTab = when {
        !isLocal -> ClipTab.OTHER_DEVICE
        item.kind == ClipKind.IMAGE || item.kind == ClipKind.FILE -> ClipTab.IMAGE_FILE
        else -> ClipTab.LOCAL
    }

    fun visibleIn(tab: ClipTab, item: ClipSyncItem, isLocal: Boolean): Boolean = when (tab) {
        ClipTab.ALL -> true
        else -> tabOf(item, isLocal) == tab
    }

    /** 差分：远端新增/版本更新视为 add，本版本冲突远端胜出（沿 ClipboardSync 口径）。 */
    fun diff(local: List<ClipSyncItem>, remote: List<ClipSyncItem>): List<ClipSyncItem> {
        val lm = local.associateBy { it.id }
        return remote.mapNotNull { r ->
            val old = lm[r.id] ?: return@mapNotNull filter(r)
            when {
                r.version > old.version -> filter(r)
                r.version == old.version && r.text != old.text -> filter(r)
                else -> null
            }
        }
    }
}
