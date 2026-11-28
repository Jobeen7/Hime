// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 合并功能总开关（纯函数，不依赖 Android，不入热路径）。
 * 重功能默认关闭，显式开启才懒加载；异常一律隔离，绝不拖垮输入法进程。
 */
object MergeFeatureGate {

    enum class Feature {
        FLOATING, CLIPBOARD_SYNC, AI_ASSISTANT, MULTILANG_ENGINE,
        EXTERNAL_ENGINE, SCRIPT_EXT, BACKUP, LAYOUT_CONVERT
    }

    private val enabled = mutableSetOf<Feature>()

    fun isEnabled(feature: Feature): Boolean = enabled.contains(feature)

    fun setEnabled(feature: Feature, on: Boolean) {
        if (on) enabled.add(feature) else enabled.remove(feature)
    }

    /** 重功能统一入口：未开启直接返回默认值且不初始化。 */
    fun <T> runIfEnabled(feature: Feature, default: T, block: () -> T): T {
        if (!isEnabled(feature)) return default
        return try {
            block()
        } catch (_: Exception) {
            default
        }
    }

    /** 仅供单测/进程重置。 */
    fun clearAll() = enabled.clear()
}
