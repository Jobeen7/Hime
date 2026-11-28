// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ScriptSandboxTest {
    private val cmds = listOf(ScriptExtension.Command("sum", 2), ScriptExtension.Command("echo", 1))
    @Test fun ok() { assertEquals(ScriptSandbox.Code.OK, ScriptSandbox.run(cmds, listOf("sum", "echo")).code) }
    @Test fun denied() { assertEquals(ScriptSandbox.Code.DENIED, ScriptSandbox.run(cmds, listOf("rm")).code) }
    @Test fun tooMany() { assertEquals(ScriptSandbox.Code.TOO_MANY, ScriptSandbox.run(cmds, List(65) { "sum" }).code) }
}
