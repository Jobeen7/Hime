package com.jobeen.hime.engine.rime.behavior

import com.jobeen.hime.engine.behavior.SelectPinYin
import com.jobeen.hime.engine.data.CandidatePinYin

class SelectPinYin(pinYin: CandidatePinYin) : SelectPinYin(pinYin), RimeBehavior by RimeBehavior.Impl() {
    override fun invoke() {
    }
}
