package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.CandidateSettingsScreen

class PredictionSettingsActivity : ImeActivity() {
    @Composable
    override fun Content() {
        CandidateSettingsScreen(onBack = { finish() })
    }
}
