// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ScriptExecutorTest {
    private val cmds = listOf(ScriptExtension.Command("echo", 1), ScriptExtension.Command("upper", 1), ScriptExtension.Command("len", 1), ScriptExtension.Command("evil", 0))
    @Test fun execOk() { val o = ScriptExecutor.exec(cmds, listOf("echo", "upper"), mapOf("echo" to "hi", "upper" to "ab")); assertEquals(ScriptSandbox.Code.OK, o.code); assertEquals("hi AB", o.value) }
    @Test fun denied() { val o = ScriptExecutor.exec(cmds, listOf("evil"), emptyMap()); assertEquals(ScriptSandbox.Code.DENIED, o.code) }
    @Test fun unknownDenied() { val o = ScriptExecutor.exec(cmds, listOf("nosuch"), emptyMap()); assertEquals(ScriptSandbox.Code.DENIED, o.code) }
    @Test fun len() { val o = ScriptExecutor.exec(cmds, listOf("len"), mapOf("len" to "hello")); assertEquals("5", o.value) }
}
