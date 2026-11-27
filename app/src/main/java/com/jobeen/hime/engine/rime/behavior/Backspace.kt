package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.Backspace
import com.jobeen.hime.engine.rime.core.KeyMapping

class Backspace : Backspace(), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {
        job?.sendJob { processKey(KeyMapping.Key_BackSpace, 0U, true) }
    }
}
