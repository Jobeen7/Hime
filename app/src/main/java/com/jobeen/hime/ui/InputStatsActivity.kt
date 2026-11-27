package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.InputStatsScreen

class InputStatsActivity : ImeActivity() {
    @Composable
    override fun Content() {
        InputStatsScreen(onBack = { finish() })
    }
}
