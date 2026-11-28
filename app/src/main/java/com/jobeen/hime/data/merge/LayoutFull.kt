// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 布局完整 schema：版本、必填键、类型与范围（纯函数）。 */
object LayoutFull {
    const val SCHEMA_VERSION = 1
    data class Verdict(val ok: Boolean, val errors: List<String>)
    fun validateFull(root: Map<String, Any?>): Verdict {
        val errors = mutableListOf<String>()
        val version = root["schemaVersion"]
        if (version !is Number || version.toInt() != SCHEMA_VERSION) errors.add("bad_version")
        val height = root["height"]
        if (height !is Number || height.toInt() !in 120..480) errors.add("bad_height")
        val rows = root["rows"]
        if (rows !is Number || rows.toInt() !in 1..12) errors.add("bad_rows")
        val theme = root["theme"]
        if (theme !is String || (theme != "light" && theme != "dark" && theme != "auto")) errors.add("bad_theme")
        val keys = root["keys"]
        if (keys !is Map<*, *>) errors.add("bad_keys") else {
            @Suppress("UNCHECKED_CAST")
            val report = LayoutSchema.validate(keys as Map<String, Any?>)
            if (!report.valid) errors.add("bad_keys:" + report.errors.firstOrNull().orEmpty())
        }
        return Verdict(errors.isEmpty(), errors.take(8))
    }
    fun default(): Map<String, Any?> = mapOf("schemaVersion" to 1, "height" to 220, "rows" to 4, "theme" to "auto", "keys" to mapOf("a" to "a", "b" to "b"))
}
