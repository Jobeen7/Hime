// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.base.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceModelCatalogTest {
    @Test fun modelsContainStreaming() { assertTrue(VoiceModelCatalog.MODELS.any { it.streaming }); assertEquals("Paraformer 流式", VoiceModelCatalog.modelById("paraformer-streaming")?.label) }
    @Test fun presetNeedsKey() { assertNull(VoiceModelCatalog.resolvePreset("qwen", null)); assertNull(VoiceModelCatalog.resolvePreset("openai", "  ")); assertEquals("通义千问语音", VoiceModelCatalog.resolvePreset("qwen", "k")?.label) }
    @Test fun unknownNull() { assertNull(VoiceModelCatalog.resolvePreset("nope", "k")); assertNull(VoiceModelCatalog.modelById("nope")) }
    @Test fun shaMatchesCaseInsensitive() { assertTrue(VoiceModelCatalog.shaMatches("A".repeat(64), "a".repeat(64))) }
}
