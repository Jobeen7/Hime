// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/** 笔迹笔画分割（纯函数）：按 PEN_DOWN 分隔、单点过滤，供手写识别调用。 */
object HandwritingStroke {
    data class Stroke(val points: List<Handwriting.Point>)
    fun split(points: List<Handwriting.Point?>, maxStrokes: Int = Handwriting.MAX_STROKES): List<Stroke> {
        val out = mutableListOf<Stroke>()
        var cur = mutableListOf<Handwriting.Point>()
        for (p in points) {
            if (p == null) {
                if (cur.size >= 2) out.add(Stroke(cur.toList()))
                cur = mutableListOf()
            } else { cur.add(p) }
            if (out.size >= maxStrokes) break
        }
        if (cur.size >= 2 && out.size < maxStrokes) out.add(Stroke(cur.toList()))
        return out.take(maxStrokes)
    }
    /** 单笔画点列归一化到 0..7 网格（供候选生成）。 */
    fun grid(points: List<Handwriting.Point>): List<Int> {
        if (points.isEmpty()) return emptyList()
        val minX = points.minOf { it.x }; val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }; val maxY = points.maxOf { it.y }
        val dx = (maxX - minX).coerceAtLeast(1); val dy = (maxY - minY).coerceAtLeast(1)
        return points.map { ((it.x - minX) * 8 / dx).coerceIn(0, 7) * 8 + ((it.y - minY) * 8 / dy).coerceIn(0, 7) }
    }
}
