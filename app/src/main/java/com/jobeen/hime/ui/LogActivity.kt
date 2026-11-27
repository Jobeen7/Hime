package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.LogScreen

class LogActivity : ImeActivity() {
    @Composable
    override fun Content() {
        LogScreen(onBack = { finish() })
    }
}
