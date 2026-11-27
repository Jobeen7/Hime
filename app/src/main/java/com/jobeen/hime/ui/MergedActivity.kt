// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.MergedScreen

/** 合并功能页宿主。 */
class MergedActivity : ImeActivity() {
    @Composable
    override fun Content() {
        MergedScreen(onBack = { finish() })
    }
}
