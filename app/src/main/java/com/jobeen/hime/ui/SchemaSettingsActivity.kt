package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.SchemaSettingsScreen

class SchemaSettingsActivity : ImeActivity() {
    @Composable
    override fun Content() {
        SchemaSettingsScreen(
            onBack = { finish() },
        )
    }

}
