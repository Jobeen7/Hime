// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test

class ScriptExecTest {
    private val cmds = listOf(
        ScriptExtension.Command("load", 1),
        ScriptExtension.Command("append", 1),
        ScriptExtension.Command("upper", 0),
        ScriptExtension.Command("lower", 0),
        ScriptExtension.Command("replace", 2),
        ScriptExtension.Command("concat", 1),
        ScriptExtension.Command("len", 0),
        ScriptExtension.Command("slice", 2),
        ScriptExtension.Command("clear", 0),
    )

    @Test fun loadUpper() {
        val o = ScriptExec.run(cmds, "", listOf("load:hello", "upper"))
        assertEquals(ScriptExec.Code.OK, o.code)
        assertEquals("HELLO", o.value)
    }

    @Test fun appendConcat() {
        val o = ScriptExec.run(cmds, "ab", listOf("append:cd", "concat:ef"))
        assertEquals("abcdef", o.value)
    }

    @Test fun replaceSlice() {
        val o = ScriptExec.run(cmds, "hello", listOf("replace:l|L", "slice:0|4"))
        assertEquals("heLL", o.value)
    }

    @Test fun lenClear() {
        assertEquals("5", ScriptExec.run(cmds, "hello", listOf("len")).value)
        assertEquals("", ScriptExec.run(cmds, "hello", listOf("clear")).value)
    }

    @Test fun deniedUnknown() {
        val o = ScriptExec.run(cmds, "", listOf("evil:rm"))
        assertEquals(ScriptExec.Code.DENIED, o.code)
    }

    @Test fun badArg() {
        assertEquals(ScriptExec.Code.BAD, ScriptExec.run(cmds, "", listOf("replace:onlyone")).code)
    }

    @Test fun tooManySteps() {
        val many = List(65) { "upper" }
        assertEquals(ScriptExec.Code.TOO_MANY, ScriptExec.run(cmds, "x", many).code)
    }
}
