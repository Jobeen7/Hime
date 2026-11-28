// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import java.io.File
/** 模型文件落地：从目录注册/校验模型文件，归一化预处理（真实文件 IO）。 */
object ModelReal {
    data class Loaded(val descriptor: ModelLoader.Descriptor, val bytes: ByteArray)
    fun registerFile(dir: File, kind: ModelLoader.Kind, name: String, version: Int): Boolean {
        if (name.isBlank() || name.contains("..") || name.contains("/") || name.contains("\\")) return false
        val f = File(dir, name)
        if (!f.exists() || !f.isFile || f.length() <= 0) return false
        return ModelLoader.register(ModelLoader.Descriptor(kind, name, f.length(), version))
    }
    fun loadFile(dir: File, name: String): Loaded? {
        if (name.isBlank() || name.contains("..")) return null
        val f = File(dir, name)
        if (!f.exists() || !f.isFile || f.length() > ModelLoader.MAX_MODEL_BYTES) return null
        val descriptor = ModelLoader.get(name) ?: return null
        return try {
            val bytes = f.readBytes()
            if (f.length() != descriptor.bytes) null else Loaded(descriptor, bytes)
        } catch (e: Exception) { null }
    }
    fun preprocess(samples: List<Int>, mean: Int, scale: Int): List<Int> = ModelLoader.normalize(samples, mean, scale)
    fun targetBox(w: Int, h: Int): Pair<Int, Int> = ModelLoader.imageBox(w, h, 96)
    fun clear() = ModelLoader.clear()
}
