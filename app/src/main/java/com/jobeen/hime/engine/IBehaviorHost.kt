package com.jobeen.hime.engine

import com.jobeen.hime.engine.behavior.IBehavior

interface IBehaviorHost {
    fun flowed(behavior: IBehavior): Boolean

    fun resetState()
}