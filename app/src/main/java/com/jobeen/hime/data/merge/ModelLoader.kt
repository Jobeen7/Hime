// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
/** 模型 loader 与预处理（纯函数）：懒加载守卫、注册表、归一化与尺寸计算；真实模型由调用方懒加载。 */
object ModelLoader {
    const val MAX_MODEL_BYTES = 256 * 1024 * 1024
    enum class Kind { HANDWRITING, OCR }
    data class Descriptor(val kind: Kind, val name: String, val bytes: Long, val version: Int)
    private val registry = mutableMapOf<String, Descriptor>()
    fun register(d: Descriptor): Boolean {
        if (d.name.isBlank() || d.bytes <= 0 || d.bytes > MAX_MODEL_BYTES || d.version <= 0) return false
        registry[d.name.trim()] = d; return true
    }
    fun get(name: String): Descriptor? = registry[name.trim()]
    fun clear() = registry.clear()
    fun normalize(samples: List<Int>, mean: Int, scale: Int): List<Int> {
        val s = if (scale == 0) 1 else scale
        return samples.take(1024).map { (it - mean) / s }
    }
    fun imageBox(w: Int, h: Int, target: Int): Pair<Int, Int> {
        if (w <= 0 || h <= 0 || target <= 0) return 0 to 0
        val ratio = w.toFloat() / h.toFloat()
        return if (ratio >= 1f) target to (target / ratio).toInt().coerceAtLeast(1) else (target * ratio).toInt().coerceAtLeast(1) to target
    }
    fun featureDim(kind: Kind): Int = when (kind) { Kind.HANDWRITING -> 64; Kind.OCR -> 128 }
}
