// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** F04 多语种 Rime 方案与释义（纯函数）：按需安装清单、事务化安装（校验失败回滚）、键位提示映射。真实下载/写盘由调用方承载。 */
object MultilangSchemes {
    data class Scheme(val id: String, val display: String, val schemaId: String, val packageFile: String, val hint: String)
    val CATALOG = listOf(
        Scheme("ja-jaroomaji", "日语 ローマ字", "rime-jaroomaji", "ja_jaroomaji.zip", "kana"),
        Scheme("ko-hangul", "韩语", "rime-hangul", "ko_hangul.zip", "european"),
        Scheme("wubi86", "五笔 86", "wubi86", "wubi86.zip", "european"),
        Scheme("wubi98", "五笔 98", "wubi98", "wubi98.zip", "european"),
        Scheme("cangjie", "仓颉", "cangjie", "cangjie.zip", "european"),
        Scheme("zhuyin", "注音", "zhuyin", "zhuyin.zip", "zhuyin"),
        Scheme("stroke", "笔画", "stroke", "stroke.zip", "european"),
        Scheme("western", "西语", "western", "western.zip", "european"),
        Scheme("fr", "法语", "fr", "fr.zip", "european"),
        Scheme("de", "德语", "de", "de.zip", "european"),
        Scheme("ru", "俄语", "ru", "ru.zip", "european"),
        Scheme("vi", "越南语", "vi", "vi.zip", "european")
    )
    fun find(id: String): Scheme? = CATALOG.find { it.id == id } ?: CATALOG.find { it.schemaId == id }
    fun layoutHint(schemeId: String): String? = find(schemeId)?.hint
    data class InstallPlan(val scheme: Scheme, val staging: String)
    fun installPlan(schemeId: String): InstallPlan? {
        val s = find(schemeId) ?: return null
        return InstallPlan(s, "staging/" + s.packageFile)
    }
    /** 事务化：校验通过才新增；失败返回 null，调用方保持原集合不变（无半成品）。 */
    fun transactionalInstall(existing: Set<String>, schemeId: String, verifyOk: Boolean): Set<String>? {
        val s = find(schemeId) ?: return null
        if (!verifyOk) return null
        if (existing.contains(s.id)) return existing
        return existing + s.id
    }
    fun uninstall(existing: Set<String>, schemeId: String): Set<String> {
        val s = find(schemeId) ?: return existing
        return existing - s.id
    }
}
