package com.jobeen.hime.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import com.jobeen.hime.ui.screen.AboutScreen

class AboutActivity : ImeActivity() {
    @Composable
    override fun Content() {
        AboutScreen(
            onBack = { finish() },
            onOpenLogs = {
                startActivity(Intent(this@AboutActivity, LogActivity::class.java))
            },
        )
    }
}
