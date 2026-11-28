// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.compat

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * F10 松烟 JSON 直读：解析松烟布局/外观文件（rows[].keys[]，label/text/width），
 * 直接在 Hime 运行时映射为 LayoutModel 规格，选即用，不经外部转换包。
 */
object SongyanLayoutReader {

    fun parse(tree: JsonObject, layoutId: String): CompatLayout? {
        val name = (tree["name"] as? JsonPrimitive)?.contentOrNull ?: layoutId
        val bg = (tree["background"] as? JsonPrimitive)?.contentOrNull
        val accent = (tree["accent"] as? JsonPrimitive)?.contentOrNull
        val rowsNode = tree["rows"] as? JsonArray ?: return null
        val rows = mutableListOf<List<CompatKey>>()
        for (rowNode in rowsNode) {
            val rowObj = rowNode as? JsonObject ?: continue
            val keysNode = rowObj["keys"] as? JsonArray ?: continue
            val row = mutableListOf<CompatKey>()
            for (keyNode in keysNode) {
                val keyObj = keyNode as? JsonObject ?: continue
                val label = (keyObj["label"] as? JsonPrimitive)?.contentOrNull
                    ?: (keyObj["text"] as? JsonPrimitive)?.contentOrNull ?: continue
                val text = (keyObj["text"] as? JsonPrimitive)?.contentOrNull ?: label
                val widthRaw = (keyObj["width"] as? JsonPrimitive)?.contentOrNull
                val width = widthRaw?.toDoubleOrNull() ?: 1.0
                row.add(CompatKey(label = label, widthWeight = width.coerceIn(0.1, 10.0), text = text))
            }
            if (row.isNotEmpty()) rows.add(row.toList())
        }
        if (rows.isEmpty()) return null
        return CompatLayout(layoutId = layoutId, name = name, rows = rows, style = CompatStyleSnapshot(backgroundPath = bg, accentColor = accent))
    }
}
