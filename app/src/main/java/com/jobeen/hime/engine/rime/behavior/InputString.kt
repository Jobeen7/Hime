package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.InputString

class InputString(override val sequence: String) : InputString(sequence), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {
        job?.sendJob { simulateKeySequence(sequence) }
    }
}
