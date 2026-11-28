// SPDX-License-Identifier: Apache-2.0

package com.jobeen.hime.data.merge

import android.content.Context

/**
 * 键序归一化桥：F02 邻近键纠错 + 模糊音归一化 接入真实输入路径的最小接线。
 * 输入路径（KeyActionListener 的 KeySequenceAction）调本桥后再进引擎；
 * 本桥只做字符级纯变换，不碰模型/连接/热路径重功能，异常隔离回原串。
 */
object MergeKeyBridge {

    /** 归一化键序：先逐键邻近纠错（a-z），再整体模糊音归一化。 */
    fun normalizeSequence(context: Context?, sequence: String): String {
        if (sequence.isEmpty()) return sequence
        return try {
            val mapped = StringBuilder(sequence.length)
            for (ch in sequence) {
                if (ch in 'a'..'z') {
                    mapped.append(
                        if (context != null) MergeWiring.neighborCorrect(context, ch)
                        else NeighborCorrection.correct(ch)
                    )
                } else {
                    mapped.append(ch)
                }
            }
            val s = mapped.toString()
            if (context != null) MergeWiring.fuzzyNormalize(context, s) else FuzzySound.normalize(s)
        } catch (_: Exception) {
            sequence
        }
    }
}
