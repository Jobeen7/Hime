package com.jobeen.hime.engine.behavior

abstract class InputKey(open val code: Int, open val modifiers: Int, open val isVirtual: Boolean) : IBehavior {}