// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MergePhase2Test {

    @Test
    fun floating_clamp_keepsInside() {
        val f = FloatingKeyboard.clamp(-100, 99999, 300, 200, 400, 800)
        assertTrue(f.x >= 0 && f.y >= 0 && f.x + f.width <= 400 && f.y + f.height <= 800)
    }

    @Test
    fun floating_sized_bounds() {
        val (w, h) = FloatingKeyboard.sized(10f, 400, 220)
        assertTrue(w in 200..1200 && h in 120..480)
    }

    @Test
    fun sync_addsNew_keepsOld() {
        val local = listOf(ClipboardSync.Entry("a", "x", 1))
        val remote = listOf(
            ClipboardSync.Entry("a", "x", 1),
            ClipboardSync.Entry("b", "y", 1)
        )
        val d = ClipboardSync.diff(local, remote)
        assertEquals(1, d.add.size)
        assertEquals("b", d.add[0].id)
    }

    @Test
    fun assistant_prompt_caps_and_intent() {
        assertEquals(Assistant.Intent.SUMMARIZE, Assistant.parseIntent("/summarize"))
        assertTrue(Assistant.buildPrompt(Assistant.Intent.UNKNOWN, "a".repeat(5000)).length <= 4000)
    }

    @Test
    fun assistant_clean_trims() {
        assertEquals("hi", Assistant.cleanReply("  hi  \n"))
    }

    @Test
    fun multilanguage_routes() {
        assertEquals("latin", MultilangEngine.route("hello"))
        assertEquals("pinyin", MultilangEngine.route("你好"))
        assertEquals("hangul", MultilangEngine.route("안녕"))
        assertEquals("pinyin", MultilangEngine.route(""))
    }

    @Test
    fun external_encode_decode_roundtrip() {
        val s = "hello world"
        assertEquals(s, ExternalEngine.decode(ExternalEngine.encode(s)))
        assertEquals("", ExternalEngine.decode(ByteArray(0)))
    }

    @Test
    fun script_validate_and_allow() {
        val cmds = listOf(ScriptExtension.Command("run", 1))
        assertNull(ScriptExtension.validate("s", "1.0", cmds))
        assertTrue(ScriptExtension.allows(cmds, "run"))
        assertFalse(ScriptExtension.allows(cmds, "evil"))
        assertTrue(ScriptExtension.validate("", "1.0", cmds) != null)
    }

    @Test
    fun backup_verify_roundtrip() {
        val m = UnifiedBackup.build(listOf(UnifiedBackup.Entry("data/a", 10, "abcd1234")))
        assertTrue(m != null && UnifiedBackup.verify(m))
        assertFalse(UnifiedBackup.verify(UnifiedBackup.Manifest(99, emptyList())))
    }

    @Test
    fun layoutconvert_yaml_ini_dvorak() {
        val keys = listOf(LayoutImport.KeyDef("A", "a"), LayoutImport.KeyDef("B", "b"))
        assertEquals(2, LayoutConvert.toYaml(keys).lines().size)
        assertTrue(LayoutConvert.toIni(keys).contains("A=a"))
        assertEquals("a", LayoutConvert.toDvorak(keys)[0].value)
        assertEquals("x", LayoutConvert.toDvorak(keys)[1].value)
    }

    @Test
    fun gate_phase2_gated() {
        MergeFeatureGate.clearAll()
        assertFalse(MergeFeatureGate.isEnabled(MergeFeatureGate.Feature.BACKUP))
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.BACKUP, true)
        assertTrue(MergeFeatureGate.isEnabled(MergeFeatureGate.Feature.BACKUP))
        MergeFeatureGate.clearAll()
    }

    @Test
    fun registry_allReady() {
        assertTrue(FeatureRegistry.readyIds().size == 14)
    }
}
