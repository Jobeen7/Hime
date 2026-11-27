package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.Selection


class Selection(override val index: Int) : Selection(index), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {
        job?.sendJob { selectCandidate(index, true) }
    }
}