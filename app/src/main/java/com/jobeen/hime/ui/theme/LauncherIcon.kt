// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui.theme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.edit

/**
 * 桌面启动器图标（logo pack v2，2026-11-28）。
 *
 * 42 个变体（月／风／翔 x 14 色）各一个 activity-alias
 * （`.ui.IconAlias_<slug>`），默认只启用风青（feng_tile_cyan），
 * 其余禁用；Flare 式单选：切换时禁用全部别名（含历史保留的
 * `.ui.LauncherAlias`）再启用所选一个，保证桌面上永远只有一个
 * Hime 入口。偏好文件 `appearance_settings`，键 `app.launcher_icon`
 * 存 slug，缺省／未知值回落风青。
 */
object LauncherIcon {
    const val DEFAULT_SLUG = "feng_tile_cyan"
    const val PREFS_NAME = "appearance_settings"
    private const val KEY_ICON = "app.launcher_icon"

    /** 42 个 slug，按 logo pack 顺序（与 manifest 一致）。 */
    val SLUGS: List<String> = listOf(
        "yue_wbg_teal",
        "yue_wbg_blue",
        "yue_wbg_ruby",
        "yue_wbg_orange",
        "yue_tile_black",
        "yue_tile_blue",
        "yue_tile_cyan",
        "yue_tile_lightblue",
        "yue_tile_orange",
        "yue_tile_red",
        "yue_tile_violet",
        "yue_tile_white",
        "yue_tile_yellow",
        "yue_tile_mint",
        "feng_wbg_teal",
        "feng_wbg_blue",
        "feng_wbg_ruby",
        "feng_wbg_orange",
        "feng_tile_black",
        "feng_tile_blue",
        "feng_tile_cyan",
        "feng_tile_lightblue",
        "feng_tile_orange",
        "feng_tile_red",
        "feng_tile_violet",
        "feng_tile_white",
        "feng_tile_yellow",
        "feng_tile_mint",
        "xiang_wbg_teal",
        "xiang_wbg_blue",
        "xiang_wbg_ruby",
        "xiang_wbg_orange",
        "xiang_tile_black",
        "xiang_tile_blue",
        "xiang_tile_cyan",
        "xiang_tile_lightblue",
        "xiang_tile_orange",
        "xiang_tile_red",
        "xiang_tile_violet",
        "xiang_tile_white",
        "xiang_tile_yellow",
        "xiang_tile_mint",
    )

    /** 历史保留别名（禁用态；图标同默认）。 */
    const val LEGACY_ALIAS = ".ui.LauncherAlias"

    /** 全部桌面入口别名（含历史保留别名），供单选切换枚举。 */
    val ALL_COMPONENTS: List<String> by lazy { SLUGS.map { aliasName(it) } + LEGACY_ALIAS }

    fun aliasName(slug: String): String = ".ui.IconAlias_$slug"

    fun parse(raw: String?): String = if (raw != null && raw in SLUGS) raw else DEFAULT_SLUG

    fun getSelected(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return parse(prefs.getString(KEY_ICON, null))
    }

    /**
     * 切换图标：exactly-one 启用所选 alias，其余禁用（含历史保留别名），
     * 并落偏好。先落偏好再切换，保证状态与桌面一致。
     */
    fun setSelected(context: Context, slug: String) {
        val chosen = parse(slug)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_ICON, chosen)
        }
        val pm = context.packageManager
        for (alias in ALL_COMPONENTS) {
            val state = if (alias == aliasName(chosen)) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            pm.setComponentEnabledSetting(
                ComponentName(context.packageName, context.packageName + alias),
                state,
                PackageManager.DONT_KILL_APP,
            )
        }
    }

    /** 当前中文名（解析 hime_icon_<slug>，缺省回风青）。 */
    fun displayName(context: Context, slug: String = getSelected(context)): String {
        val name = "hime_icon_" + parse(slug)
        val res = context.resources
        val id = res.getIdentifier(name, "string", context.packageName)
        return if (id != 0) res.getString(id) else parse(slug)
    }
}
