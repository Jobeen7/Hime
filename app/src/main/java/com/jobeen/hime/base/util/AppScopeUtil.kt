package com.jobeen.hime.base.util


import com.jobeen.hime.ImeApplication
import kotlinx.coroutines.CoroutineScope

val appScope: CoroutineScope get() = ImeApplication.getInstance().applicationScope
