package com.jobeen.hime.data.merge

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** 接线桥纯委托单测：门控关闭返回默认值且不初始化，开启后透传模块结果。 */
class MergeWiringTest {

    @After
    fun tearDown() {
        MergeFeatureGate.clearAll()
        NeighborCorrection.clearAll()
    }

    @Test
    fun gatedOff_returnsDefaults() {
        assertEquals("", MergeWiring.assistantPrompt(Assistant.Intent.SUMMARIZE, "text"))
        assertEquals(MultilangEngine.DEFAULT_ENGINE, MergeWiring.multilangRoute("hello"))
        assertEquals(ByteArray(0).size, MergeWiring.externalEncode("hi").size)
        assertEquals("", MergeWiring.externalDecode(ByteArray(0)))
        assertTrue(MergeWiring.parseLayoutYaml("a: b").isEmpty())
        assertTrue(MergeWiring.handwritingNormalize(listOf(Handwriting.Point(0, 0))).isEmpty())
        assertTrue(MergeWiring.ocrParse("line").isEmpty())
        assertFalse(MergeWiring.backupVerify(UnifiedBackup.Manifest(1, emptyList())))
        assertEquals("disabled", MergeWiring.scriptValidate("n", "1", listOf(ScriptExtension.Command("c", 0))))
        assertEquals(FloatingKeyboard.Frame(0, 0, 0, 0), MergeWiring.floatingFrame(dummyContext(), 0, 0, 10, 10, 100, 100))
    }

    @Test
    fun gatedOn_delegatesToModules() {
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.AI_ASSISTANT, true)
        assertTrue(MergeWiring.assistantPrompt(Assistant.Intent.SUMMARIZE, "text").contains("text"))
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.LAYOUT_CONVERT, true)
        assertEquals(listOf(LayoutImport.KeyDef("a", "b")), MergeWiring.parseLayoutYaml("a: b"))
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.FLOATING, true)
        val frame = MergeWiring.floatingFrame(dummyContext(), -50, -50, 320, 220, 1080, 1920)
        assertTrue(frame.x >= FloatingKeyboard.MARGIN)
        assertTrue(frame.y >= FloatingKeyboard.MARGIN)
        MergeFeatureGate.setEnabled(MergeFeatureGate.Feature.MULTILANG_ENGINE, true)
        assertEquals("latin", MergeWiring.multilangRoute("hello"))
    }

    @Test
    fun statusLines_coverRegistry() {
        assertEquals(FeatureRegistry.entries.size, MergeWiring.statusLines().size)
    }

    @Test
    fun schemePresets_includeWanxiang() {
        assertEquals("wanxiang", MergeWiring.schemePreset("wanxiang")?.id)
        assertEquals(8, MergeWiring.schemePresets().size)
    }

    // floatingFrame 门控路径不读偏好，ContextWrapper 空壳即可（不调用其任何方法）。
    private fun dummyContext(): android.content.Context =
        object : android.content.ContextWrapper(null) {}
}
