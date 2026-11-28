package com.jobeen.hime.base.util

import android.text.InputType

/**
 * 输入法布局决策（纯函数，可单测；脱离 Android 框架）。
 *
 * 按官方指南把 EditorInfo.inputType 的类与变体划成键盘布局：
 * - 数字／电话类强制数字键盘；
 * - 文本类密码（普通／可见／Web）强制英文键盘直输；
 * - 其余（含日期时间、TYPE_NULL 等未单独处理类）走当前布局，
 *   并在字段变化时按登录账号豁免规则决定是否复位方案。
 *
 * 本对象不碰 View／引擎，只回答该用哪种布局；落布局的副作用
 * 留在 KeyboardStateManager。位运算只吃 inputType 整数，
 * EditorInfo 本身不进 JVM 单测（与 InputFieldPolicy 同口径）。
 *
 * 对照：
 * - Create an input method：检测 inputType 并提供对应界面。
 * - InputMethodService：TYPE_NULL 只支持原始键事件——当前仍走
 *   普通布局（未单独分支），见 PR 说明；本函数不改该行为。
 * - Android Keyboard Design Guide：UI 层与核心业务逻辑分离、
 *   业务逻辑脱离框架可测。
 */
object InputModeResolver {

    /** 强制布局：无强制时跟当前方案布局。 */
    enum class ForcedLayout { NUMBER, QWERTY, NONE }

    /** 一次输入开始的布局决策。 */
    data class Resolution(
        val forcedLayout: ForcedLayout,
        val resetToFirstSchema: Boolean,
    )

    fun resolve(inputType: Int, fieldChanged: Boolean): Resolution {
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        // 场景强制优先：数字／电话不被自创布局替换。
        // 数字密码 variation 也落此分支（与旧 isPasswordText 的
        // NUMBER 先行口径一致）。
        if (
            inputClass == InputType.TYPE_CLASS_NUMBER ||
            inputClass == InputType.TYPE_CLASS_PHONE
        ) {
            return Resolution(ForcedLayout.NUMBER, resetToFirstSchema = false)
        }
        // 文本类密码强制英文：只认 TEXT 类的三种密码变体
        // （InputFieldPolicy.isPasswordField 在此的入参非 NUMBER
        // 类，只能是 TEXT 密码）。
        if (InputFieldPolicy.isPasswordField(inputType)) {
            return Resolution(ForcedLayout.QWERTY, resetToFirstSchema = false)
        }
        // 普通布局分支：登录账号框豁免新窗口复位（与
        // KeyboardStateManager 原分支一致）。
        val reset = fieldChanged && !InputFieldPolicy.isAccountField(inputType)
        return Resolution(ForcedLayout.NONE, resetToFirstSchema = reset)
    }
}
