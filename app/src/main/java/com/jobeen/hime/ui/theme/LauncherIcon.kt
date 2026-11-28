// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui.theme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * 桌面图标变体（logo pack：3 形 × 14 色 ＝ 42 变体）。
 *
 * 默认变体单弧 teal 由原 `.ui.LauncherAlias` 承载（其图标已换成
 * `@mipmap/ic_single_wbg_teal`）；其余 41 个变体各由一个
 * `.IconAlias_<slug>` activity-alias 承载，切换时一次只启用一个
 * （含 LauncherAlias 互斥），不做常驻逻辑。
 */
data class LauncherIconVariant(
    val slug: String,
    val label: String,
) {
    /** manifest 中 activity-alias 的完整类名（包名 com.jobeen.hime）。 */
    val aliasClassName: String
        get() = "com.jobeen.hime.IconAlias_$slug"
}

object LauncherIcon {
    /** 默认变体（单弧 teal），由 LauncherAlias 承载。 */
    const val DEFAULT_SLUG = "single_wbg_teal"

    /** 默认启动入口别名（唯一 LAUNCHER，图标已是单弧 teal）。 */
    const val LAUNCHER_ALIAS_CLASS = "com.jobeen.hime.ui.LauncherAlias"

    /** 42 个变体（顺序与 AndroidManifest.xml 中 alias 顺序一致）。 */
    val VARIANTS: List<LauncherIconVariant> = listOf(
        LauncherIconVariant("single_wbg_teal", "单弧 · 白底 teal"),
        LauncherIconVariant("single_wbg_blue", "单弧 · 白底蓝"),
        LauncherIconVariant("single_wbg_ruby", "单弧 · 白底红宝石"),
        LauncherIconVariant("single_wbg_orange", "单弧 · 白底橙"),
        LauncherIconVariant("single_tile_black", "单弧 · 黑底"),
        LauncherIconVariant("single_tile_blue", "单弧 · 蓝底"),
        LauncherIconVariant("single_tile_cyan", "单弧 · 青底"),
        LauncherIconVariant("single_tile_lightblue", "单弧 · 浅蓝底"),
        LauncherIconVariant("single_tile_orange", "单弧 · 橙底"),
        LauncherIconVariant("single_tile_red", "单弧 · 红底"),
        LauncherIconVariant("single_tile_violet", "单弧 · 紫底"),
        LauncherIconVariant("single_tile_white", "单弧 · 白底"),
        LauncherIconVariant("single_tile_yellow", "单弧 · 黄底"),
        LauncherIconVariant("single_tile_mint", "单弧 · 薄荷底"),
        LauncherIconVariant("diagonal_wbg_teal", "斜线 · 白底 teal"),
        LauncherIconVariant("diagonal_wbg_blue", "斜线 · 白底蓝"),
        LauncherIconVariant("diagonal_wbg_ruby", "斜线 · 白底红宝石"),
        LauncherIconVariant("diagonal_wbg_orange", "斜线 · 白底橙"),
        LauncherIconVariant("diagonal_tile_black", "斜线 · 黑底"),
        LauncherIconVariant("diagonal_tile_blue", "斜线 · 蓝底"),
        LauncherIconVariant("diagonal_tile_cyan", "斜线 · 青底"),
        LauncherIconVariant("diagonal_tile_lightblue", "斜线 · 浅蓝底"),
        LauncherIconVariant("diagonal_tile_orange", "斜线 · 橙底"),
        LauncherIconVariant("diagonal_tile_red", "斜线 · 红底"),
        LauncherIconVariant("diagonal_tile_violet", "斜线 · 紫底"),
        LauncherIconVariant("diagonal_tile_white", "斜线 · 白底"),
        LauncherIconVariant("diagonal_tile_yellow", "斜线 · 黄底"),
        LauncherIconVariant("diagonal_tile_mint", "斜线 · 薄荷底"),
        LauncherIconVariant("triple_wbg_teal", "三弧 · 白底 teal"),
        LauncherIconVariant("triple_wbg_blue", "三弧 · 白底蓝"),
        LauncherIconVariant("triple_wbg_ruby", "三弧 · 白底红宝石"),
        LauncherIconVariant("triple_wbg_orange", "三弧 · 白底橙"),
        LauncherIconVariant("triple_tile_black", "三弧 · 黑底"),
        LauncherIconVariant("triple_tile_blue", "三弧 · 蓝底"),
        LauncherIconVariant("triple_tile_cyan", "三弧 · 青底"),
        LauncherIconVariant("triple_tile_lightblue", "三弧 · 浅蓝底"),
        LauncherIconVariant("triple_tile_orange", "三弧 · 橙底"),
        LauncherIconVariant("triple_tile_red", "三弧 · 红底"),
        LauncherIconVariant("triple_tile_violet", "三弧 · 紫底"),
        LauncherIconVariant("triple_tile_white", "三弧 · 白底"),
        LauncherIconVariant("triple_tile_yellow", "三弧 · 黄底"),
        LauncherIconVariant("triple_tile_mint", "三弧 · 薄荷底"),
    )

    /** 按 slug 解析变体：未知／空 → 默认变体（纯函数，可 JVM 单测）。 */
    fun variantForSlug(slug: String?): LauncherIconVariant =
        VARIANTS.firstOrNull { it.slug == slug }
            ?: VARIANTS.first { it.slug == DEFAULT_SLUG }

    /** 按 slug 解析在 VARIANTS 中的下标：未知／空 → 默认下标（纯函数）。 */
    fun indexForSlug(slug: String?): Int =
        VARIANTS.indexOfFirst { it.slug == slug }
            .takeIf { it >= 0 }
            ?: VARIANTS.indexOfFirst { it.slug == DEFAULT_SLUG }

    /** 按 slug 取显示名：未知／空 → 默认显示名（纯函数）。 */
    fun labelForSlug(slug: String?): String = variantForSlug(slug).label

    private fun component(context: Context, className: String): ComponentName =
        ComponentName(context.packageName, className)

    /**
     * 切换桌面图标：先禁用全部 42 个 IconAlias 与 LauncherAlias，
     * 再启用目标变体对应的那一个（默认变体启用 LauncherAlias）。
     * 用 DONT_KILL_APP，不杀进程、不重启、无常驻逻辑。
     */
    fun setVariant(context: Context, slug: String) {
        val pm = context.packageManager
        val target = variantForSlug(slug)
        for (variant in VARIANTS) {
            pm.setComponentEnabledSetting(
                component(context, variant.aliasClassName),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )
        }
        pm.setComponentEnabledSetting(
            component(context, LAUNCHER_ALIAS_CLASS),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
        val targetClass = if (target.slug == DEFAULT_SLUG) {
            LAUNCHER_ALIAS_CLASS
        } else {
            target.aliasClassName
        }
        pm.setComponentEnabledSetting(
            component(context, targetClass),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    /**
     * 读当前桌面图标变体：有启用的 IconAlias 返回其 slug；
     * 否则（默认 LauncherAlias 启用／都未显式启用）返回默认 slug。
     */
    fun getCurrentVariant(context: Context): String {
        val pm = context.packageManager
        for (variant in VARIANTS) {
            if (pm.getComponentEnabledSetting(component(context, variant.aliasClassName)) ==
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            ) {
                return variant.slug
            }
        }
        return DEFAULT_SLUG
    }
}
