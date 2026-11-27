package com.jobeen.hime.input.pinner

import android.content.Context
import android.view.View
import com.jobeen.hime.engine.data.EngineMessage

interface IPinner {
    val view: View
    fun updateDynamicPreedit(items: List<EngineMessage.DynamicPreedit.DynamicPreeditItem>)
    fun refreshTheme(context: Context)
}
