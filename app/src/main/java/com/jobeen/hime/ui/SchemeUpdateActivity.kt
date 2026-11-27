package com.jobeen.hime.ui

import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.SchemeUpdateScreen

class SchemeUpdateActivity : ImeActivity() {
    @Composable
    override fun Content() {
        SchemeUpdateScreen(onBack = { finish() })
    }
}
