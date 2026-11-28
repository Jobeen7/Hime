// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 布局 schema 校验（纯函数）：嵌套 map 展开、深度/键数上限、必填与类型检查。 */
object LayoutSchema {
    const val MAX_KEYS = 256
    const val MAX_DEPTH = 4
    data class Report(val valid: Boolean, val errors: List<String>, val flat: List<LayoutImport.KeyDef>)
    fun validate(root: Map<String, Any?>, depth: Int = 0): Report {
        val errors = mutableListOf<String>()
        val flat = mutableListOf<LayoutImport.KeyDef>()
        if (depth > MAX_DEPTH) return Report(false, listOf("too_deep"), emptyList())
        if (root.size > MAX_KEYS) errors.add("too_many_keys")
        for ((k, v) in root) {
            if (k.isBlank() || v == null) { errors.add("blank_or_null:$k"); continue }
            when (v) {
                is String -> if (v.isBlank() || v.length > 128) errors.add("bad_value:$k") else flat.add(LayoutImport.KeyDef(k, v))
                is Number -> flat.add(LayoutImport.KeyDef(k, v.toString()))
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val sub = validate(v as Map<String, Any?>, depth + 1)
                    if (!sub.valid) errors.add("nested:$k")
                    flat.addAll(sub.flat)
                }
                else -> errors.add("unsupported:$k")
            }
            if (flat.size > MAX_KEYS) { errors.add("too_many_flat"); break }
        }
        if (flat.isEmpty()) errors.add("empty")
        return Report(errors.isEmpty(), errors.take(16), flat.take(MAX_KEYS))
    }
}
