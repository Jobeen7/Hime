// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * OCR 图像预处理（纯函数）：灰度化、Otsu 自适应阈值、二值化、裁剪与最近邻缩放。
 * 输入为 ARGB IntArray 的行优先像素；输出为 0..255 的 ByteArray 行优先。
 * 仅做几何与像素变换，不加载识别模型，模型由调用方懒加载。
 */
object OcrPipeline {
    const val MAX_PIXELS = 4_000_000

    class Preprocessed(val width: Int, val height: Int, val threshold: Int, val binary: ByteArray)

    /** ARGB -> 灰度 0..255：Y = (299R + 587G + 114B) / 1000，Alpha 忽略。 */
    fun toGray(argb: IntArray): ByteArray {
        if (argb.isEmpty()) return ByteArray(0)
        val out = ByteArray(argb.size)
        for (i in argb.indices) {
            val c = argb[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            out[i] = ((r * 299 + g * 587 + b * 114) / 1000).toByte()
        }
        return out
    }

    /** Otsu 最大类间方差阈值；空图返回 128；单值图返回该值。 */
    fun otsuThreshold(gray: ByteArray): Int {
        if (gray.isEmpty()) return 128
        val hist = IntArray(256)
        for (v in gray) hist[v.toInt() and 0xFF]++
        var sum = 0L
        for (i in 0..255) sum += i.toLong() * hist[i]
        val total = gray.size.toLong()
        var sumB = 0L
        var wB = 0L
        var maxVar = -1.0
        var best = 128
        for (i in 0..255) {
            wB += hist[i]
            if (wB == 0L) continue
            val wF = total - wB
            if (wF == 0L) break
            sumB += i.toLong() * hist[i]
            val mB = sumB.toDouble() / wB.toDouble()
            val mF = (sum - sumB).toDouble() / wF.toDouble()
            val between = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)
            if (between > maxVar) { maxVar = between; best = i }
        }
        if (maxVar < 0.0) return gray[0].toInt() and 0xFF
        return best
    }

    /** 二值化：大于阈值 255，否则 0。 */
    fun binarize(gray: ByteArray, threshold: Int): ByteArray {
        val t = threshold.coerceIn(0, 255)
        val out = ByteArray(gray.size)
        for (i in gray.indices) out[i] = if ((gray[i].toInt() and 0xFF) > t) 255.toByte() else 0
        return out
    }

    /** 裁剪框钳制后的实际宽高（非法返回 0 to 0）。 */
    fun croppedSize(w: Int, h: Int, x: Int, y: Int, cw: Int, ch: Int): Pair<Int, Int> {
        if (w <= 0 || h <= 0 || cw <= 0 || ch <= 0) return 0 to 0
        val sx = x.coerceIn(0, w)
        val sy = y.coerceIn(0, h)
        val rw = minOf(cw, w - sx)
        val rh = minOf(ch, h - sy)
        if (rw <= 0 || rh <= 0) return 0 to 0
        return rw to rh
    }

    /** 裁剪灰度子图（行优先），越界钳制；非法返回空数组。 */
    fun cropGray(gray: ByteArray, w: Int, h: Int, x: Int, y: Int, cw: Int, ch: Int): ByteArray {
        if (w <= 0 || h <= 0 || gray.size < w * h) return ByteArray(0)
        val (rw, rh) = croppedSize(w, h, x, y, cw, ch)
        if (rw == 0 || rh == 0) return ByteArray(0)
        val sx = x.coerceIn(0, w)
        val sy = y.coerceIn(0, h)
        val out = ByteArray(rw * rh)
        for (row in 0 until rh) {
            val src = (sy + row) * w + sx
            System.arraycopy(gray, src, out, row * rw, rw)
        }
        return out
    }

    /** 最近邻缩放（行优先），目标宽高钳制 >=1；非法返回空数组。 */
    fun resizeNearest(gray: ByteArray, w: Int, h: Int, newW: Int, newH: Int): ByteArray {
        if (w <= 0 || h <= 0 || gray.size < w * h || newW <= 0 || newH <= 0) return ByteArray(0)
        val out = ByteArray(newW * newH)
        for (dy in 0 until newH) {
            val sy = (dy * h / newH).coerceIn(0, h - 1)
            for (dx in 0 until newW) {
                val sx = (dx * w / newW).coerceIn(0, w - 1)
                out[dy * newW + dx] = gray[sy * w + sx]
            }
        }
        return out
    }

    /** 完整预处理：灰度 -> Otsu -> 二值化；尺寸非法/像素超上限返回 null。 */
    fun preprocess(argb: IntArray, w: Int, h: Int): Preprocessed? {
        if (w <= 0 || h <= 0) return null
        if (argb.size < w * h) return null
        if (w * h > MAX_PIXELS) return null
        val gray = toGray(argb.sliceArray(0 until w * h))
        val threshold = otsuThreshold(gray)
        val binary = binarize(gray, threshold)
        return Preprocessed(w, h, threshold, binary)
    }
}
