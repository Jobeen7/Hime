package com.jobeen.hime.input.keyboard.impl

import com.jobeen.hime.engine.data.CandidatePinYin

interface ISidePanelKeyboard : IKeyboard {
    fun onPossibleCandidatePinYin(data: List<CandidatePinYin>)
}
