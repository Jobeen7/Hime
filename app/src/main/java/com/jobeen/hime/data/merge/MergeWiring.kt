// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import android.content.Context

/**
 * 合并功能接线桥：Android 侧唯一入口。
 * 负责把 SharedPreferences（"merge_prefs"，键见 [MergePrefs]）同步到
 * [MergeFeatureGate]，并把各纯函数模块按门控暴露给设置页与输入法进程。
 * 本类不做重初始化：重功能未开启时直接返回默认值且不碰模型/连接，
 * 输入法热路径零负担；异常一律隔离，绝不拖垮输入进程。
 */
object MergeWiring {

    const val PREFS_NAME = "merge_prefs"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 从偏好同步全部 8 个重功能门控；进程启动与设置页打开时调用。 */
    fun syncFromPrefs(context: Context) {
        val p = prefs(context)
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.FLOATING,
            p.getBoolean(MergePrefs.KEY_FLOATING_ON, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.CLIPBOARD_SYNC,
            p.getBoolean(MergePrefs.KEY_CLIPBOARD_SYNC, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.AI_ASSISTANT,
            p.getBoolean(MergePrefs.KEY_AI, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.MULTILANG_ENGINE,
            p.getBoolean(MergePrefs.KEY_MULTILANG, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.EXTERNAL_ENGINE,
            p.getBoolean(MergePrefs.KEY_EXTERNAL, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.SCRIPT_EXT,
            p.getBoolean(MergePrefs.KEY_SCRIPT_ALLOW, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.BACKUP,
            p.getBoolean(MergePrefs.KEY_BACKUP_ON, false),
        )
        MergeFeatureGate.setEnabled(
            MergeFeatureGate.Feature.LAYOUT_CONVERT,
            p.getBoolean(MergePrefs.KEY_LAYOUT, false),
        )
    }

    /** 设置页开关：写偏好并立即同步门控。 */
    fun setFeature(context: Context, feature: MergeFeatureGate.Feature, on: Boolean) {
        prefs(context).edit().putBoolean(prefKeyFor(feature), on).apply()
        MergeFeatureGate.setEnabled(feature, on)
    }

    private fun prefKeyFor(feature: MergeFeatureGate.Feature): String = when (feature) {
        MergeFeatureGate.Feature.FLOATING -> MergePrefs.KEY_FLOATING_ON
        MergeFeatureGate.Feature.CLIPBOARD_SYNC -> MergePrefs.KEY_CLIPBOARD_SYNC
        MergeFeatureGate.Feature.AI_ASSISTANT -> MergePrefs.KEY_AI
        MergeFeatureGate.Feature.MULTILANG_ENGINE -> MergePrefs.KEY_MULTILANG
        MergeFeatureGate.Feature.EXTERNAL_ENGINE -> MergePrefs.KEY_EXTERNAL
        MergeFeatureGate.Feature.SCRIPT_EXT -> MergePrefs.KEY_SCRIPT_ALLOW
        MergeFeatureGate.Feature.BACKUP -> MergePrefs.KEY_BACKUP_ON
        MergeFeatureGate.Feature.LAYOUT_CONVERT -> MergePrefs.KEY_LAYOUT
    }

    /** 设置页初始快照：先同步再读门控。 */
    fun gateSnapshot(context: Context): Map<MergeFeatureGate.Feature, Boolean> {
        syncFromPrefs(context)
        return MergeFeatureGate.Feature.entries.associateWith { MergeFeatureGate.isEnabled(it) }
    }

    fun isNeighborCorrectEnabled(context: Context): Boolean =
        prefs(context).getBoolean(MergePrefs.KEY_NEIGHBOR_CORRECT, true)

    fun isFuzzyEnabled(context: Context): Boolean =
        prefs(context).getBoolean(MergePrefs.KEY_FUZZY_SOUND, true)

    fun isClipboardChunkEnabled(context: Context): Boolean =
        prefs(context).getBoolean(MergePrefs.KEY_CLIPBOARD_CHUNK_ON, false)

    fun currentScheme(context: Context): String =
        prefs(context).getString(MergePrefs.KEY_SCHEME, "wanxiang") ?: "wanxiang"

    fun floatingScale(context: Context): Float =
        prefs(context).getFloat(MergePrefs.KEY_FLOATING_SCALE, FloatingKeyboard.SCALE_MEDIUM)

    // ── 轻量功能（无重依赖，随开关即用） ──

    /** 剪贴板长文本分块：开关关闭时整段返回（保持旧行为）。 */
    fun chunkClipboard(context: Context, text: String): List<String> {
        if (!isClipboardChunkEnabled(context)) {
            return if (text.isEmpty()) emptyList() else listOf(text)
        }
        return ClipboardChunker.chunk(text)
    }

    fun fuzzyNormalize(context: Context, pinyin: String): String =
        if (isFuzzyEnabled(context)) FuzzySound.normalize(pinyin) else pinyin.lowercase()

    fun neighborCorrect(context: Context, key: Char): Char =
        if (isNeighborCorrectEnabled(context)) NeighborCorrection.correct(key)
        else key.lowercaseChar()

    fun neighborRecord(context: Context, wrong: Char, intended: Char) {
        if (isNeighborCorrectEnabled(context)) NeighborCorrection.recordMistake(wrong, intended)
    }

    fun schemePreset(id: String): SchemePresets.Preset? = SchemePresets.byId(id)

    fun schemePresets(): List<SchemePresets.Preset> = SchemePresets.all

    // ── 重功能（门控，未开启返回默认值且不初始化） ──

    fun floatingFrame(
        context: Context, x: Int, y: Int, w: Int, h: Int, screenW: Int, screenH: Int,
    ): FloatingKeyboard.Frame = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.FLOATING,
        FloatingKeyboard.Frame(0, 0, 0, 0),
    ) {
        FloatingKeyboard.clamp(x, y, w, h, screenW, screenH)
    }

    fun floatingSize(context: Context, screenW: Int, baseHeight: Int): Pair<Int, Int> =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.FLOATING,
            0 to 0,
        ) {
            FloatingKeyboard.sized(floatingScale(context), screenW, baseHeight)
        }

    fun clipboardSyncDiff(
        local: List<ClipboardSync.Entry>, remote: List<ClipboardSync.Entry>,
    ): ClipboardSync.Diff = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.CLIPBOARD_SYNC,
        ClipboardSync.Diff(emptyList(), emptyList(), emptyList()),
    ) {
        ClipboardSync.diff(local, remote)
    }

    fun assistantPrompt(intent: Assistant.Intent, contextText: String): String =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.AI_ASSISTANT, "",
        ) {
            Assistant.buildPrompt(intent, contextText)
        }

    /** 命令字面量解析（纯函数，无门控）：设置页自检调用，防止整函数被裁。 */
    fun assistantParseIntent(command: String): String =
        Assistant.parseIntent(command).name

    /** 回复清洗（纯函数，无门控）：设置页自检调用，防止整函数被裁。 */
    fun assistantCleanReply(reply: String): String =
        Assistant.cleanReply(reply)

    fun multilangRoute(text: String): String = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.MULTILANG_ENGINE, MultilangEngine.DEFAULT_ENGINE,
    ) {
        MultilangEngine.route(text)
    }

    fun externalEncode(text: String): ByteArray = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.EXTERNAL_ENGINE, ByteArray(0),
    ) {
        ExternalEngine.encode(text)
    }

    fun externalDecode(bytes: ByteArray): String = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.EXTERNAL_ENGINE, "",
    ) {
        ExternalEngine.decode(bytes)
    }

    fun scriptValidate(
        name: String, version: String, commands: List<ScriptExtension.Command>,
    ): String? = MergeFeatureGate.runIfEnabled(
        MergeFeatureGate.Feature.SCRIPT_EXT, "disabled",
    ) {
        ScriptExtension.validate(name, version, commands)
    }

    fun backupBuild(entries: List<UnifiedBackup.Entry>): UnifiedBackup.Manifest? =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.BACKUP, null,
        ) {
            UnifiedBackup.build(entries)
        }

    fun backupVerify(manifest: UnifiedBackup.Manifest): Boolean =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.BACKUP, false,
        ) {
            UnifiedBackup.verify(manifest)
        }

    fun parseLayoutYaml(text: String): List<LayoutImport.KeyDef> =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.LAYOUT_CONVERT, emptyList(),
        ) {
            LayoutImport.parseYaml(text)
        }

    fun parseLayoutIni(text: String): List<LayoutImport.KeyDef> =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.LAYOUT_CONVERT, emptyList(),
        ) {
            LayoutImport.parseIni(text)
        }

    fun layoutToYaml(keys: List<LayoutImport.KeyDef>): String =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.LAYOUT_CONVERT, "",
        ) {
            LayoutConvert.toYaml(keys)
        }

    fun layoutToDvorak(keys: List<LayoutImport.KeyDef>): List<LayoutImport.KeyDef> =
        MergeFeatureGate.runIfEnabled(
            MergeFeatureGate.Feature.LAYOUT_CONVERT, emptyList(),
        ) {
            LayoutConvert.toDvorak(keys)
        }

    // ── 状态（设置页展示） ──

    fun featureEntries(): List<FeatureRegistry.Entry> = FeatureRegistry.entries

    fun statusLines(): List<String> =
        FeatureRegistry.entries.map { "${it.id} · ${it.status}" }
}
