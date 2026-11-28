// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ParallelAiTest {
    @Test fun assistant_caps() {
        assertTrue(Assistant.buildPrompt(Assistant.Intent.SUMMARIZE, "x".repeat(5000)).length <= Assistant.MAX_PROMPT)
        assertTrue(Assistant.cleanReply("  a  \n b \n").length <= Assistant.MAX_REPLY)
        assertEquals(Assistant.Intent.TRANSLATE, Assistant.parseIntent("/translate"))
    }
    @Test fun script_allows() {
        val cs = listOf(ScriptExtension.Command("say_ok", 0))
        assertTrue(ScriptExtension.allows(cs, "say_ok"))
        assertFalse(ScriptExtension.allows(cs, "nope"))
        assertNull(ScriptExtension.validate("s", "1", cs))
    }
    @Test fun backup_roundtrip() {
        val m = UnifiedBackup.build(listOf(UnifiedBackup.Entry("data/a", 1, "abcd1234")))
        assertNotNull(m); assertTrue(UnifiedBackup.verify(m!!))
    }
}
