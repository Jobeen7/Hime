// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui.screen

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
import com.jobeen.hime.data.merge.MergeFeatureGate
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
                            stringResource(R.string.merged_empty_result)
                        } else {
                            keys.joinToString("\n") { "${it.label}: ${it.value}" }
                        }
                    },
                ) { Text(stringResource(R.string.merged_parse_yaml)) }
                Button(
                    onClick = {
                        val keys: List<LayoutImport.KeyDef> = MergeWiring.parseLayoutIni(layoutText)
                        layoutResult = if (keys.isEmpty()) {
                            stringResource(R.string.merged_empty_result)
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
        }
    }
}
