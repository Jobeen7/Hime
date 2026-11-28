// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge

/**
 * 悬浮窗物理（纯函数）：拖动钳制、磁吸、轻推滑动（fling）与命中测试。
 * 补充 FloatingState.snap 的单步吸附：这里补速度衰减轨迹与边墙停止，
 * 供视图层逐帧消费；无 Android 依赖，步数有上限，不分配热点。
 */
object FloatingPhysics {
    const val MAX_VELOCITY = 60f
    const val FRICTION = 0.92f
    const val STOP_VELOCITY = 0.5f
    const val MAX_STEPS = 64
    const val SNAP_THRESHOLD = 40

    data class Velocity(val vx: Float, val vy: Float)
    data class Position(val x: Int, val y: Int)

    fun coerceVelocity(vx: Float, vy: Float): Velocity {
        val sx = MAX_VELOCITY
        return Velocity(vx.coerceIn(-sx, sx), vy.coerceIn(-sx, sx))
    }

    fun clampDrag(x: Int, y: Int, w: Int, h: Int, screenW: Int, screenH: Int): Position {
        val f = FloatingKeyboard.clamp(x, y, w, h, screenW, screenH)
        return Position(f.x, f.y)
    }

    /** 水平磁吸：阈值内贴最近边，否则保持（钳制后返回）。 */
    fun snapX(x: Int, w: Int, screenW: Int, threshold: Int = SNAP_THRESHOLD): Int {
        val safeW = if (w <= 0) 320 else w
        val margin = FloatingKeyboard.MARGIN
        val t = threshold.coerceAtLeast(0)
        val target = when {
            x <= margin + t -> margin
            x + safeW >= screenW - margin - t -> screenW - safeW - margin
            else -> x
        }
        val maxX = maxOf(margin, screenW - safeW - margin)
        return target.coerceIn(margin, maxX)
    }

    fun snap(p: Position, w: Int, h: Int, screenW: Int, screenH: Int, threshold: Int = SNAP_THRESHOLD): Position {
        val clamped = clampDrag(p.x, p.y, w, h, screenW, screenH)
        return Position(snapX(clamped.x, w, screenW, threshold), clamped.y)
    }

    fun hitTest(px: Int, py: Int, wx: Int, wy: Int, ww: Int, wh: Int): Boolean {
        if (ww <= 0 || wh <= 0) return false
        return px >= wx && px < wx + ww && py >= wy && py < wy + wh
    }

    fun hitTest(px: Int, py: Int, frame: FloatingKeyboard.Frame): Boolean =
        hitTest(px, py, frame.x, frame.y, frame.width, frame.height)

    /** 单步轻推：位置 += 速度，撞墙钳制并清零该轴速度，速度按摩擦衰减。 */
    fun flingStep(p: Position, v: Velocity, w: Int, h: Int, screenW: Int, screenH: Int): Pair<Position, Velocity> {
        val nx = p.x + v.vx.toInt()
        val ny = p.y + v.vy.toInt()
        val clamped = clampDrag(nx, ny, w, h, screenW, screenH)
        val nvx = if (clamped.x != nx) 0f else v.vx * FRICTION
        val nvy = if (clamped.y != ny) 0f else v.vy * FRICTION
        return clamped to Velocity(nvx, nvy)
    }

    /** 完整轻推轨迹（不含起点），步数上限 MAX_STEPS，速度降到 STOP_VELOCITY 即停。 */
    fun simulateFling(start: Position, vel: Velocity, w: Int, h: Int, screenW: Int, screenH: Int): List<Position> {
        var pos = start
        var cur = coerceVelocity(vel.vx, vel.vy)
        if (abs(cur.vx) < STOP_VELOCITY && abs(cur.vy) < STOP_VELOCITY) return emptyList()
        val out = mutableListOf<Position>()
        repeat(MAX_STEPS) {
            val step = flingStep(pos, cur, w, h, screenW, screenH)
            pos = step.first
            cur = step.second
            out.add(pos)
            if (abs(cur.vx) < STOP_VELOCITY && abs(cur.vy) < STOP_VELOCITY) return out
        }
        return out
    }

    private fun abs(v: Float): Float = if (v < 0f) -v else v
}
