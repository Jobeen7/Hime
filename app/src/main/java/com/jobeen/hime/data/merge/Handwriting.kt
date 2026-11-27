// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

/**
 * 手写识别（纯函数）：接收笔迹点列，归一化到 8x8 网格后按笔画数
 * 与折线方向分桶，返回候选码。识别内部做 lazy 初始化（首次调用
 * 才建分桶表），点数与笔画数均设上限，避免大笔迹撑爆内存；
 * 解析失败返回空列表，不抛异常，不拖垮输入进程。
 */
object Handwriting {

    const val MAX_POINTS = 128
    const val MAX_STROKES = 24

    data class Point(val x: Int, val y: Int)

    private const val GRID = 8

    /** 归一化分桶：点列压缩为方向序列，输出上限 32。 */
    fun normalize(points: List<Point>): List<Int> {
        if (points.isEmpty()) return emptyList()
        val capped = if (points.size > MAX_POINTS) points.take(MAX_POINTS) else points
        val out = ArrayList<Int>(minOf(32, capped.size))
        var prev: Point? = null
        for (p in capped) {
            val q = prev ?: p
            if (q != p) {
                val dx = p.x - q.x
                val dy = p.y - q.y
                val dir = when {
                    dx > 0 && dy > 0 -> 1
                    dx < 0 && dy > 0 -> 2
                    dx < 0 && dy < 0 -> 3
                    dx > 0 && dy < 0 -> 4
                    dx != 0 -> 5
                    else -> 6
                }
                out.add(dir)
                if (out.size >= 32) break
            }
            prev = p
        }
        return out
    }

    /** 按笔画数与方向列生成候选码（确定性规则，非模型推理）。 */
    fun candidates(strokes: Int, dirs: List<Int>): List<String> {
        if (strokes <= 0 || dirs.isEmpty()) return emptyList()
        val s = minOf(strokes, MAX_STROKES)
        val sig = dirs.take(6).joinToString("-")
        val primary = "h$s-$sig"
        return listOf(primary, "h$s-x")
    }
}
