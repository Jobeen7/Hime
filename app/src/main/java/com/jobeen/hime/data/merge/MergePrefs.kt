// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 合并偏好键名（纯常量）：统一各功能模块的设置键，避免散落硬编码。
 */
object MergePrefs {

    const val KEY_NEIGHBOR_CORRECT = "merge_neighbor_correct"
    const val KEY_FUZZY_SOUND = "merge_fuzzy_sound"
    const val KEY_LAYOUT_SOURCE = "merge_layout_source"
    const val KEY_CLIPBOARD_CHUNK_ON = "merge_clipboard_chunk_on"
    const val KEY_SCHEME = "merge_scheme"
    const val KEY_HEAVY_FEATURES = "merge_heavy_features"

    fun defaultFor(key: String): String = when (key) {
        KEY_NEIGHBOR_CORRECT -> "true"
        KEY_FUZZY_SOUND -> "true"
        KEY_CLIPBOARD_CHUNK_ON -> "false"
        KEY_SCHEME -> "wanxiang"
        else -> ""
    }
}
