package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.Reset

class Reset() : Reset(), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {
        job?.sendJob { clearComposition() }
    }
}