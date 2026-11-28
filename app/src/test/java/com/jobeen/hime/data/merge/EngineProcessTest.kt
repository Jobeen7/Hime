// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class EngineProcessTest {
    @Test fun crashRestarts() { assertTrue(EngineProcess.shouldRestart(0, false)) }
    @Test fun cleanExitsStop() { assertFalse(EngineProcess.shouldRestart(0, true)) }
    @Test fun attemptsExhausted() { assertFalse(EngineProcess.shouldRestart(3, false)) }
    @Test fun backoff() { assertEquals(1000L, EngineProcess.delayMs(1)) }
    @Test fun pid() { assertTrue(EngineProcess.pidValid(123)); assertFalse(EngineProcess.pidValid(0)) }
    @Test fun onExit() { assertEquals(EngineProcess.Action.RESTART, EngineProcess.onExit(0, false)); assertEquals(EngineProcess.Action.STOP, EngineProcess.onExit(0, true)) }
}
