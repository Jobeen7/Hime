// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 合并功能登记表（纯函数）：把 53 项待合并清单收敛为可门控的
 * 功能大类，状态供设置页展示与后续移植里程碑使用。
 * 本表仅做登记，不加载任何重依赖，性能影响可忽略。
 */
object FeatureRegistry {

    enum class Category { LAYOUT, SCHEME, INPUT_STYLE, HANDWRITE_OCR, FLOATING, CLIPBOARD, BACKUP, AI_PLUGIN, ENGINE }

    enum class Status { READY, GATED, PLANNED }

    data class Entry(val id: String, val category: Category, val status: Status)

    val entries: List<Entry> = listOf(
        Entry("layout_yaml_import", Category.LAYOUT, Status.READY),
        Entry("layout_ini_import", Category.LAYOUT, Status.READY),
        Entry("layout_converter", Category.LAYOUT, Status.READY),
        Entry("scheme_presets", Category.SCHEME, Status.READY),
        Entry("neighbor_correct", Category.INPUT_STYLE, Status.READY),
        Entry("fuzzy_sound", Category.INPUT_STYLE, Status.READY),
        Entry("clipboard_chunk", Category.CLIPBOARD, Status.READY),
        Entry("handwriting", Category.HANDWRITE_OCR, Status.READY),
        Entry("ocr", Category.HANDWRITE_OCR, Status.READY),
        Entry("floating_keyboard", Category.FLOATING, Status.READY),
        Entry("clipboard_sync", Category.CLIPBOARD, Status.READY),
        Entry("ai_assistant", Category.AI_PLUGIN, Status.READY),
        Entry("multilang_engine", Category.ENGINE, Status.READY),
        Entry("external_rime_ipc", Category.ENGINE, Status.READY),
        Entry("script_extension", Category.AI_PLUGIN, Status.READY),
        Entry("backup_unified", Category.BACKUP, Status.READY)
    )

    fun byCategory(category: Category): List<Entry> = entries.filter { it.category == category }

    fun readyIds(): List<String> = entries.filter { it.status == Status.READY }.map { it.id }
}
