// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 剪贴板同步（纯函数）：本地与远端条目做集合差（新增/删除/冲突），
 * 冲突按版本号取新者。条目上限 256，文本单条上限 2000 字符；
 * 不访问系统剪贴板，不注册监听器，同步由调用方显式触发，
 * 避免后台常驻耗电与内存。
 */
object ClipboardSync {

    const val MAX_ENTRIES = 256
    const val MAX_TEXT_LEN = 2000

    data class Entry(val id: String, val text: String, val version: Int)

    data class Diff(val add: List<Entry>, val removeIds: List<String>, val keep: List<Entry>)

    fun diff(local: List<Entry>, remote: List<Entry>): Diff {
        // 超长文本截断保留（原先整条丢弃会丢同步内容）；id 空白丢弃。
        val l = local.distinctBy { it.id }.take(MAX_ENTRIES)
            .filter { it.id.isNotBlank() }.map { it.copy(text = it.text.take(MAX_TEXT_LEN)) }
        val r = remote.distinctBy { it.id }.take(MAX_ENTRIES)
            .filter { it.id.isNotBlank() }.map { it.copy(text = it.text.take(MAX_TEXT_LEN)) }
        val lMap = l.associateBy { it.id }
        val rMap = r.associateBy { it.id }
        val add = mutableListOf<Entry>()
        val remove = mutableListOf<String>()
        val keep = mutableListOf<Entry>()
        for ((id, e) in rMap) {
            val old = lMap[id]
            when {
                old == null -> add.add(e)
                e.version > old.version -> add.add(e)
                // 同版本但文本不同视为冲突，远端胜出（原先静默保留本地会丢更新）
                e.version == old.version && e.text != old.text -> add.add(e)
                else -> keep.add(old)
            }
        }
        for (id in lMap.keys) if (!rMap.containsKey(id)) remove.add(id)
        return Diff(add.take(MAX_ENTRIES), remove.take(MAX_ENTRIES), keep.take(MAX_ENTRIES))
    }
}
