// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 方案预设目录（纯函数）：8 个方案的静态目录。
 * 方案名（万象/雾凇/小鹤拼音等）属于用户可达的方案名，予以保留；
 * 本目录只承载展示与安装入口，不内置词库数据。
 */
object SchemePresets {

    data class Preset(val id: String, val name: String, val packageName: String)

    val all: List<Preset> = listOf(
        Preset("wanxiang", "万象拼音", "wanxiang"),
        Preset("wusong", "雾凇拼音", "rime-ice"),
        Preset("xiaohe", "小鹤拼音", "xiaohe"),
        Preset("wubi", "五笔", "wubi"),
        Preset("bihu", "笔画", "stroke"),
        Preset("cangjie", "仓颉", "cangjie"),
        Preset("zhuyin", "注音", "zhuyin"),
        Preset("jyutping", "粤语拼音", "jyutping")
    )

    fun byId(id: String): Preset? = all.firstOrNull { it.id == id }

    fun byName(name: String): Preset? = all.firstOrNull { it.name == name }
}
