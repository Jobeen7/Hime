package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.KeyboardSettingsScreen

class KeyboardSettingsActivity : ImeActivity() {
    override val useEdgeToEdge = false

    @Composable
    override fun Content() {
        KeyboardSettingsScreen(
            onBack = { finish() },
        )
    }
}
