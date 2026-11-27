package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.Segmentation

class Segmentation : Segmentation(), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {}
}
