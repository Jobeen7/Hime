// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 53 项缺口修复的纯函数回归（无需设备；设备功能不在此宣称通过）。 */
class MergeGapRepairTest {

    @Test
    fun sync_sameVersionDifferentText_remoteWins() {
        val d = ClipboardSync.diff(
            listOf(ClipboardSync.Entry("a", "old", 1)),
            listOf(ClipboardSync.Entry("a", "new", 1)),
        )
        assertEquals(1, d.add.size)
        assertEquals("new", d.add[0].text)
    }

    @Test
    fun sync_overlongText_truncatedNotDropped() {
        val long = "x".repeat(5000)
        val d = ClipboardSync.diff(emptyList(), listOf(ClipboardSync.Entry("a", long, 1)))
        assertEquals(1, d.add.size)
        assertEquals(ClipboardSync.MAX_TEXT_LEN, d.add[0].text.length)
    }

    @Test
    fun fuzzy_expandedInitials() {
        assertEquals("zan", FuzzySound.normalize("san"))
        assertEquals("hang", FuzzySound.normalize("fang"))
        assertEquals("ban", FuzzySound.normalize("pan"))
        assertEquals("hello", FuzzySound.normalize("hello"))
    }

    @Test
    fun layoutYaml_inlineCommentQuotesListPrefix() {
        val keys = LayoutImport.parseYaml("A: a # comment\n- B: \"b\"\nC: 'c' # x\n")
        assertEquals(3, keys.size)
        assertEquals("a", keys[0].value)
        assertEquals("b", keys[1].value)
        assertEquals("c", keys[2].value)
    }

    @Test
    fun layoutYaml_duplicateKeepsFirst() {
        val keys = LayoutImport.parseYaml("A: a\nA: b\n")
        assertEquals(1, keys.size)
        assertEquals("a", keys[0].value)
    }

    @Test
    fun layoutIni_quotedValue() {
        val keys = LayoutImport.parseIni("[r]\nA=\"a b\"\n")
        assertEquals(1, keys.size)
        assertEquals("a b", keys[0].value)
    }

    @Test
    fun backup_rejectsBadChecksumDupAndTraversal() {
        assertNull(UnifiedBackup.build(listOf(UnifiedBackup.Entry("data/a", 10, "zzzzzzzz"))))
        assertNull(UnifiedBackup.build(listOf(UnifiedBackup.Entry("../etc", 10, "abcd1234"))))
        assertNull(
            UnifiedBackup.build(
                listOf(
                    UnifiedBackup.Entry("data/a", 10, "abcd1234"),
                    UnifiedBackup.Entry("data/a", 10, "abcd1234"),
                ),
            ),
        )
    }

    @Test
    fun script_rejectsUnsafeCommandName() {
        val bad = listOf(ScriptExtension.Command("../evil", 1))
        assertTrue(ScriptExtension.validate("s", "1.0", bad) != null)
        val good = listOf(ScriptExtension.Command("say_ok-1", 1))
        assertNull(ScriptExtension.validate("s", "1.0", good))
    }

    @Test
    fun ocr_parse_overallCap() {
        val raw = (1..100).joinToString("\n") { "l" + "x".repeat(100) }
        val segs = OcrResult.parse(raw)
        val total = segs.joinToString("").length
        assertTrue(total <= OcrResult.MAX_CHARS)
        assertTrue(segs.isNotEmpty())
    }

    @Test
    fun handwriting_candidates_remainsRulePlaceholder() {
        // 无真机/模型时仅规则分桶，不得宣称为真实识别通过
        assertTrue(Handwriting.candidates(3, listOf(1, 2)).isNotEmpty())
    }

    @Test
    fun floating_and_assistant_guardsHold() {
        assertFalse(FloatingKeyboard.clamp(0, 0, 0, 0, 400, 800).width <= 0)
        assertTrue(Assistant.buildPrompt(Assistant.Intent.SUMMARIZE, "x").isNotEmpty())
    }
}
