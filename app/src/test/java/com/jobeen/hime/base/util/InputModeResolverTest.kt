package com.jobeen.hime.base.util

import android.text.InputType
import com.jobeen.hime.base.util.InputModeResolver.ForcedLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * InputModeResolver 决策锁定（与 KeyboardStateManager.startInput
 * 旧内联口径逐项一致）：数字／电话强制数字盘、文本密码强制英文、
 * 普通字段跟当前布局且字段变化时按账号豁免决定复位。
 */
class InputModeResolverTest {

    private fun text(variation: Int): Int = InputType.TYPE_CLASS_TEXT or variation

    private fun number(variation: Int): Int = InputType.TYPE_CLASS_NUMBER or variation

    @Test
    fun numberFieldForcesNumber() {
        val r = InputModeResolver.resolve(
            number(InputType.TYPE_NUMBER_VARIATION_NORMAL),
            fieldChanged = true,
        )
        assertEquals(ForcedLayout.NUMBER, r.forcedLayout)
        assertFalse(r.resetToFirstSchema)
    }

    @Test
    fun phoneFieldForcesNumber() {
        val r = InputModeResolver.resolve(
            InputType.TYPE_CLASS_PHONE,
            fieldChanged = true,
        )
        assertEquals(ForcedLayout.NUMBER, r.forcedLayout)
        assertFalse(r.resetToFirstSchema)
    }

    @Test
    fun numberPasswordForcesNumberNotQwerty() {
        val r = InputModeResolver.resolve(
            number(InputType.TYPE_NUMBER_VARIATION_PASSWORD),
            fieldChanged = true,
        )
        assertEquals(ForcedLayout.NUMBER, r.forcedLayout)
        assertFalse(r.resetToFirstSchema)
    }

    @Test
    fun textPasswordsForceQwerty() {
        val variations = listOf(
            InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
        )
        for (v in variations) {
            val r = InputModeResolver.resolve(text(v), fieldChanged = true)
            assertEquals(ForcedLayout.QWERTY, r.forcedLayout)
            assertFalse(r.resetToFirstSchema)
        }
    }

    @Test
    fun normalTextUsesCurrentLayoutAndResetsOnChange() {
        val r = InputModeResolver.resolve(
            text(InputType.TYPE_TEXT_VARIATION_NORMAL),
            fieldChanged = true,
        )
        assertEquals(ForcedLayout.NONE, r.forcedLayout)
        assertTrue(r.resetToFirstSchema)
    }

    @Test
    fun normalTextWithoutChangeDoesNotReset() {
        val r = InputModeResolver.resolve(
            text(InputType.TYPE_TEXT_VARIATION_NORMAL),
            fieldChanged = false,
        )
        assertEquals(ForcedLayout.NONE, r.forcedLayout)
        assertFalse(r.resetToFirstSchema)
    }

    @Test
    fun accountFieldIsExemptFromReset() {
        val variations = listOf(
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
            InputType.TYPE_TEXT_VARIATION_URI,
        )
        for (v in variations) {
            val r = InputModeResolver.resolve(text(v), fieldChanged = true)
            assertEquals(ForcedLayout.NONE, r.forcedLayout)
            assertFalse(r.resetToFirstSchema)
        }
    }

    @Test
    fun datetimeUsesCurrentLayout() {
        val r = InputModeResolver.resolve(
            InputType.TYPE_CLASS_DATETIME,
            fieldChanged = true,
        )
        assertEquals(ForcedLayout.NONE, r.forcedLayout)
        assertTrue(r.resetToFirstSchema)
    }

    @Test
    fun typeNullUsesCurrentLayoutPreservingOldBehavior() {
        val r = InputModeResolver.resolve(0, fieldChanged = true)
        assertEquals(ForcedLayout.NONE, r.forcedLayout)
        // 旧内联口径：TYPE_NULL 落 else 分支，字段变化即复位；
        // 本函数不新设 TYPE_NULL 分支，保持原行为。
        assertTrue(r.resetToFirstSchema)
    }
}
