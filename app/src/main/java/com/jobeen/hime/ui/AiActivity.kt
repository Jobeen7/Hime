// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.AiSettingsScreen

/** 命令面板管理页宿主（WO52 9.1，设置一级入口 `ai`）。 */
class AiActivity : ImeActivity() {

    @Composable
    override fun Content() {
        AiSettingsScreen(
            onBack = { finish() },
        )
    }
}
