// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.base.speech

/**
 * F11 语音清单扩充（纯数据目录，不触网络）：在既有 sherpa-onnx 架构上
 * 扩展模型清单（SenseVoice / Paraformer 流式切换），并补充云端语音脚本
 * 预设模板（通义千问/豆包/OpenAI/SiliconFlow）。
 * 密钥加密存储由调用方负责：本目录只记录是否需要密钥、端点模板与安全提示；
 * 无密钥时 resolvePreset 返回 null 并提示，不发起网络请求。
 */
data class VoiceModelEntry(
    val id: String,
    val label: String,
    val components: List<String>,
    val streaming: Boolean,
    val requiresTokens: Boolean,
)

data class VoicePresetTemplate(
    val id: String,
    val label: String,
    val endpointTemplate: String,
    val keyHint: String,
    val requiresKey: Boolean = true,
)

object VoiceModelCatalog {
    val MODELS: List<VoiceModelEntry> = listOf(
        VoiceModelEntry("zipformer-streaming", "Zipformer 流式（既有）", listOf("encoder", "decoder", "joiner"), true, true),
        VoiceModelEntry("sensevoice", "SenseVoice", listOf("encoder", "decoder", "joiner"), false, true),
        VoiceModelEntry("paraformer-streaming", "Paraformer 流式", listOf("encoder", "decoder", "joiner"), true, true),
    )

    val PRESETS: List<VoicePresetTemplate> = listOf(
        VoicePresetTemplate("qwen", "通义千问语音", "https://dashscope.aliyuncs.com/api/v1/audio/transcription", "DashScope API Key"),
        VoicePresetTemplate("doubao", "豆包语音", "https://api.doubao.example.invalid/v1/audio", "豆包 Access Key"),
        VoicePresetTemplate("openai", "OpenAI 语音", "https://api.openai.com/v1/audio/transcriptions", "OpenAI API Key"),
        VoicePresetTemplate("siliconflow", "SiliconFlow 语音", "https://api.siliconflow.cn/v1/audio/transcriptions", "SiliconFlow API Key"),
    )

    fun modelById(id: String): VoiceModelEntry? = MODELS.firstOrNull { it.id == id }

    /** 无密钥返回 null（调用方提示，不请求网络）；有非空密钥才返回预设。 */
    fun resolvePreset(id: String, key: String?): VoicePresetTemplate? {
        val p = PRESETS.firstOrNull { it.id == id } ?: return null
        val k = key?.trim()
        return if (p.requiresKey && (k == null || k.isEmpty())) null else p
    }

    /** SHA-256  hex 校验（空白/大小写不敏感），用于下载后验包。 */
    fun shaMatches(actualHex: String, expectedHex: String): Boolean =
        actualHex.trim().equals(expectedHex.trim(), ignoreCase = true) && expectedHex.trim().matches(Regex("[0-9a-fA-F]{64}"))

    fun safetyHint(): String = "无密钥时只提示，不请求网络；密钥勿明文落盘。"
}
