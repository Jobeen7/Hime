package com.jobeen.hime.input.keyboard.key

fun interface KeyActionListener {
    fun onKeyAction(action: KeyboardAction)

    companion object {
        val Empty = KeyActionListener {}
    }
}
