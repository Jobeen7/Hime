// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.compat

import com.jobeen.hime.skin.layout.LayoutKeyModel
import com.jobeen.hime.skin.layout.LayoutModel
import com.jobeen.hime.skin.model.SkinAction

/** 兼容层中间规格：三个直读器（YAML 主题 / BDS 皮肤包 / 自定义 JSON）共用，再映射到 LayoutModel。 */
data class CompatKey(val label: String, val widthWeight: Double = 1.0, val text: String = "")
data class CompatStyleSnapshot(val backgroundPath: String? = null, val accentColor: String? = null, val mappingText: String = "")
data class CompatLayout(val layoutId: String, val name: String, val rows: List<List<CompatKey>>, val style: CompatStyleSnapshot)

object CompatLayoutMapper {
    /** 把中间规格映射为 Hime 运行时可消费的 LayoutModel（按键动作 = Text 直送组字）。 */
    fun toLayoutModel(layout: CompatLayout): LayoutModel {
        val rows = layout.rows.map { row ->
            row.mapIndexed { idx, key ->
                val label = key.label.ifBlank { key.text }
                LayoutKeyModel(
                    stableId = "${layout.layoutId}-${row === layout.rows.first()}-${idx}-${key.label.hashCode()}",
                    label = label,
                    widthWeight = key.widthWeight.coerceAtLeast(0.1),
                    rowSpan = 1,
                    alignSpanEnd = false,
                    tap = SkinAction.Text(key.text.ifBlank { label }),
                    swipeUp = null,
                    swipeDown = null,
                    longPress = null
                )
            }
        }
        return LayoutModel(layoutId = layout.layoutId, name = layout.name, rows = rows)
    }
}
