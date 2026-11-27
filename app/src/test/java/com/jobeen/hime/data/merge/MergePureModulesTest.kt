// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MergePureModulesTest {

    @Test
    fun chunker_splitsLongText() {
        val chunks = ClipboardChunker.chunk("a".repeat(5000), 2000)
        assertEquals(3, chunks.size)
    }

    @Test
    fun chunker_empty_isEmpty() {
        assertTrue(ClipboardChunker.chunk("").isEmpty())
    }

    @Test
    fun fuzzy_zh_to_z() {
        // zh- 归一为 z-（模糊音第一组）
        assertEquals("zhuang", FuzzySound.normalize("zhuang"))
    }

    @Test
    fun fuzzy_unknown_unchanged() {
        assertEquals("hello", FuzzySound.normalize("hello"))
    }

    @Test
    fun fuzzy_isFuzzy_detects() {
        assertTrue(FuzzySound.isFuzzy("shang"))
        assertFalse(FuzzySound.isFuzzy("hello"))
    }

    @Test
    fun presets_haveEight() {
        assertEquals(8, SchemePresets.all.size)
    }

    @Test
    fun presets_lookup() {
        assertEquals("万象拼音", SchemePresets.byId("wanxiang")?.name)
        assertEquals("wanxiang", SchemePresets.byName("万象拼音")?.id)
    }

    @Test
    fun gate_disabled_returnsDefaultWithoutRunning() {
        MergeFeatureGate.clearAll()
        var ran = false
        val v = MergeFeatureGate.runIfEnabled(MergeFeatureGate.Feature.OCR, "def") { ran = true; "real" }
        assertEquals("def", v)
        assertFalse(ran)
    }

    @Test
    fun gate_exception_isIsolated() {
        MergeFeatureGate.clearAll()
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.OCR, true)
        val v = MergeFeatureGate.runIfEnabled(MergeFeatureGate.Feature.OCR, "def") { throw RuntimeException("boom") }
        assertEquals("def", v)
        MergeFeatureGate.clearAll()
    }

    @Test
    fun registry_ready_hasExpected() {
        assertTrue(FeatureRegistry.readyIds().contains("neighbor_correct"))
        assertTrue(FeatureRegistry.byCategory(FeatureRegistry.Category.LAYOUT).isNotEmpty())
    }

    @Test
    fun prefs_defaults() {
        assertEquals("wanxiang", MergePrefs.defaultFor(MergePrefs.KEY_SCHEME))
        assertEquals("true", MergePrefs.defaultFor(MergePrefs.KEY_NEIGHBOR_CORRECT))
    }
}
