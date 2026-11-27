// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jobeen.hime.R
import com.jobeen.hime.data.merge.Assistant
import com.jobeen.hime.data.merge.ClipboardSync
import com.jobeen.hime.data.merge.MergeFeatureGate
import com.jobeen.hime.data.merge.OcrResult
import com.jobeen.hime.data.merge.ScriptExtension
import com.jobeen.hime.data.merge.UnifiedBackup
import com.jobeen.hime.data.merge.MergeWiring
import com.jobeen.hime.data.merge.LayoutImport

/**
 * 合并功能页：10 个重功能的门控开关（写 "merge_prefs" 并即时同步门控）、
 * 功能登记状态，以及布局导入的实际解析演示。纯设置层，
 * 不碰键盘服务与引擎热路径。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergedScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var gates by remember { mutableStateOf(MergeWiring.gateSnapshot(context)) }
    var layoutText by remember { mutableStateOf("") }
    var layoutResult by remember { mutableStateOf("") }
    var checkResult by remember { mutableStateOf("") }
    val emptyResultText = stringResource(R.string.merged_empty_result)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.merged_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.merged_switches))
            for (feature in MergeFeatureGate.Feature.entries) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(feature.name)
                    Switch(
                        checked = gates[feature] == true,
                        onCheckedChange = { on ->
                            MergeWiring.setFeature(context, feature, on)
                            gates = gates + (feature to on)
                        },
                    )
                }
            }
            Text(stringResource(R.string.merged_status))
            for (line in MergeWiring.statusLines()) {
                Text(line)
            }
            Text(stringResource(R.string.merged_layout))
            OutlinedTextField(
                value = layoutText,
                onValueChange = { layoutText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.merged_layout_hint)) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val keys: List<LayoutImport.KeyDef> = MergeWiring.parseLayoutYaml(layoutText)
                        layoutResult = if (keys.isEmpty()) {
                            emptyResultText
                        } else {
                            keys.joinToString("\n") { "${it.label}: ${it.value}" }
                        }
                    },
                ) { Text(stringResource(R.string.merged_parse_yaml)) }
                Button(
                    onClick = {
                        val keys: List<LayoutImport.KeyDef> = MergeWiring.parseLayoutIni(layoutText)
                        layoutResult = if (keys.isEmpty()) {
                            emptyResultText
                        } else {
                            keys.joinToString("\n") { "${it.label}: ${it.value}" }
                        }
                    },
                ) { Text(stringResource(R.string.merged_parse_ini)) }
            }
            if (layoutResult.isNotEmpty()) {
                Text(layoutResult)
            }
            Text(stringResource(R.string.merged_floating))
            val size = MergeWiring.floatingSize(context, 1080, 400)
            Text("${size.first} x ${size.second}")
            Text(stringResource(R.string.merged_selfcheck))
            Button(
                onClick = { checkResult = runSelfCheck(context) },
            ) { Text(stringResource(R.string.merged_selfcheck_run)) }
            if (checkResult.isNotEmpty()) {
                Text(checkResult)
            }
        }
    }
}

/** 模块自检：经 MergeWiring 逐项调用全部 18 个模块（样本输入），结果只影响设置页展示；单个模块异常不影响其余行。 */
private fun runSelfCheck(context: Context): String {
    val sb = StringBuilder()
    fun line(label: String, block: () -> Any?) {
        sb.append(label).append(": ")
        sb.append(runCatching(block).getOrElse { "ERR:${it.javaClass.simpleName}" }).append("\n")
    }
    line("neighbor") { MergeWiring.neighborCorrect(context, 'g') }
    line("fuzzy") { MergeWiring.fuzzyNormalize(context, "zhuang") }
    line("chunk") { MergeWiring.chunkClipboard(context, "Hime ".repeat(300)).size }
    line("scheme") { MergeWiring.currentScheme(context) }
    line("presets") { MergeWiring.schemePresets().joinToString(",") { it.name } }
    line("handwriting") { MergeWiring.handwritingCandidates(3, listOf(0, 1, 2, 3)) }
    line("hwNormalize") {
        MergeWiring.handwritingNormalize(
            listOf(Handwriting.Point(0, 0), Handwriting.Point(3, 4), Handwriting.Point(9, 2)),
        ).size
    }
    line("ocr") { MergeWiring.ocrParse("Hime input method") }
    line("ocrFilter") {
        MergeWiring.ocrFilter(listOf(OcrResult.Segment("line1", 0.9f), OcrResult.Segment("low", 0.1f)))
    }
    line("assistant") { MergeWiring.assistantPrompt(Assistant.Intent.SUMMARIZE, "demo context").take(40) }
    line("assistantAll") {
        listOf(
            Assistant.Intent.TRANSLATE,
            Assistant.Intent.REWRITE,
            Assistant.Intent.UNKNOWN,
        ).joinToString("|") { MergeWiring.assistantPrompt(it, "demo context").take(24) }
    }
    line("assistantParse") {
        listOf("/summarize", "/translate", "/rewrite", "总结", "翻译", "改写", "xyz")
            .joinToString(",") { MergeWiring.assistantParseIntent(it) }
    }
    line("assistantClean") { MergeWiring.assistantCleanReply("  hello \nworld  ").length }
    line("multilang") { MergeWiring.multilangRoute("hello") }
    line("external") { MergeWiring.externalDecode(MergeWiring.externalEncode("Hime")) }
    line("script") { (MergeWiring.scriptValidate("demo", "1.0", listOf(ScriptExtension.Command("say", 1))) ?: "ok") }
    line("scriptBadName") {
        MergeWiring.scriptValidate("demo", "1.0", listOf(ScriptExtension.Command("x".repeat(40), 1)))
    }
    line("scriptBadCount") {
        MergeWiring.scriptValidate("demo", "1.0", listOf(ScriptExtension.Command("say", 99)))
    }
    line("backup") {
        val m = MergeWiring.backupBuild(listOf(UnifiedBackup.Entry("rime.lua", 42, "deadbeef")))
        if (m == null) "gated-off" else "verify=${MergeWiring.backupVerify(m)}"
    }
    line("clipsync") {
        val d = MergeWiring.clipboardSyncDiff(
            listOf(ClipboardSync.Entry("1", "a", 1)),
            listOf(ClipboardSync.Entry("1", "a", 1), ClipboardSync.Entry("2", "b", 1)),
        )
        "add=${d.add.size}"
    }
    line("layout") { MergeWiring.layoutToYaml(listOf(LayoutImport.KeyDef("a", "b"))) }
    line("dvorak") {
        MergeWiring.layoutToDvorak(
            listOf(LayoutImport.KeyDef("a", "b"), LayoutImport.KeyDef("c", "d")),
        ).size
    }
    line("ocrJob") { MergeWiring.ocrJob("img-1").toString() }
    line("features") { MergeWiring.featureEntries().size }
    line("neighborRec") {
        MergeWiring.neighborRecord(context, 'g', 'f')
        MergeWiring.neighborCorrect(context, 'g')
    }
    line("presetOne") { MergeWiring.schemePreset("wusong")?.name ?: "none" }
    line("gates") {
        listOf(
            MergeWiring.isNeighborCorrectEnabled(context),
            MergeWiring.isFuzzyEnabled(context),
            MergeWiring.isClipboardChunkEnabled(context),
        ).toString()
    }
    line("floating") { MergeWiring.floatingFrame(context, 10, 10, 200, 100, 1080, 1920).toString() }
    return sb.toString().trimEnd()
}
