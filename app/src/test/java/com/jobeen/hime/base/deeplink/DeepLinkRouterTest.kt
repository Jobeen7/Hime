package com.jobeen.hime.base.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** WO52 4.2：deep link 路由逐项单测（17 别名＋2 特例＋回落 24 入口＋解析口径）。 */
class DeepLinkRouterTest {

    private fun resolve(uri: String) =
        DeepLinkRouter.resolve(DeepLinkRouter.ACTION_VIEW, uri)

    private fun pageOf(uri: String): String? = resolve(uri)?.pageKey

    // ---- 解析口径 ----

    @Test
    fun `non view action is discarded`() {
        assertNull(DeepLinkRouter.resolve("android.intent.action.SEND", "hime://com.jobeen.hime/rime"))
        assertNull(DeepLinkRouter.resolve(null, "hime://com.jobeen.hime/rime"))
    }

    @Test
    fun `empty data string is discarded`() {
        assertNull(DeepLinkRouter.resolve(DeepLinkRouter.ACTION_VIEW, null))
        assertNull(DeepLinkRouter.resolve(DeepLinkRouter.ACTION_VIEW, ""))
    }

    @Test
    fun `wrong scheme or host is discarded`() {
        assertNull(pageOf("other://com.jobeen.hime/rime"))
        assertNull(pageOf("hime://other.host/rime"))
        assertNull(pageOf("not-a-uri"))
    }

    @Test
    fun `scheme is case-insensitive`() {
        assertEquals("rime", pageOf("HIME://com.jobeen.hime/rime"))
        assertEquals("rime", pageOf("hime://com.jobeen.hime/rime"))
    }

    @Test
    fun `trailing slash is stripped`() {
        assertEquals("rime", pageOf("hime://com.jobeen.hime/rime/"))
        assertEquals("rime", pageOf("hime://com.jobeen.hime/rime///"))
    }

    @Test
    fun `empty path is discarded`() {
        assertNull(pageOf("hime://com.jobeen.hime"))
        assertNull(pageOf("hime://com.jobeen.hime/"))
    }

    @Test
    fun `query key and value are both decoded`() {
        val target = resolve("hime://com.jobeen.hime/phrase?id=42&name=a+b&x=%26")!!
        assertEquals("42", target.params["id"])
        // 值与键同口径解码（2026-11-16 N9）：+ 先换 %2B 再解码，
        // 故值里的 + 仍是字面加号；%26 解成 &。
        assertEquals("a+b", target.params["name"])
        assertEquals("&", target.params["x"])
        // 值里的 %20 解成空格、中文 %XX 解成原文。
        val target2 = resolve(
            "hime://com.jobeen.hime/phrase?title=%E4%B8%AD%E6%96%87%20a",
        )!!
        assertEquals("中文 a", target2.params["title"])
    }

    @Test
    fun `query key plus stays plus per spec`() {
        // 单子口径：键 URLDecoder.decode 前 + 先换成 %2B，
        // 故键里的 + 解出来仍是 +，不是空格。
        val target = resolve("hime://com.jobeen.hime/phrase?my+key=v")!!
        assertEquals("v", target.params["my+key"])
        assertNull(target.params["my key"])
        // %20 照常解成空格。
        val target2 = resolve("hime://com.jobeen.hime/phrase?my%20key=v")!!
        assertEquals("v", target2.params["my key"])
    }

    @Test
    fun `empty query gives empty params`() {
        assertEquals(emptyMap<String, String>(), resolve("hime://com.jobeen.hime/rime")!!.params)
    }

    @Test
    fun `key without value gives empty value`() {
        assertEquals("", resolve("hime://com.jobeen.hime/rime?flag")!!.params["flag"])
    }

    // ---- 2 特例 ----

    @Test
    fun `keyboard settings with numeric symbols goes to keyboard page`() {
        assertEquals(
            "keyboard",
            pageOf("hime://com.jobeen.hime/keyboardSettings?action=numericSymbols"),
        )
        assertEquals(
            "keyboard",
            pageOf("hime://com.jobeen.hime/keyboardSettings?action=t9Symbols"),
        )
    }

    @Test
    fun `clipboard with phrase action goes to phrase page`() {
        assertEquals(
            "phrase",
            pageOf("hime://com.jobeen.hime/clipboard?action=phrase"),
        )
    }

    @Test
    fun `clipboard without phrase action stays on clipboard page`() {
        assertEquals("clipboard", pageOf("hime://com.jobeen.hime/clipboard"))
        assertEquals(
            "clipboard",
            pageOf("hime://com.jobeen.hime/clipboard?action=clipboard&id=7"),
        )
    }

    @Test
    fun `clipboard id param is passed through for the entry landing`() {
        val target = resolve("hime://com.jobeen.hime/clipboard?id=7")
        assertEquals("clipboard", target?.pageKey)
        assertEquals("7", target?.params?.get("id"))
    }

    // ---- 15 别名 ----

    @Test
    fun `fifteen aliases map to pages`() {
        val expected = mapOf(
            "inputSchema" to "schema",
            "finder" to "files",
            "keyboardSettings" to "keyboard",
            "inputBehavior" to "keyboard",
            "feedback" to "keyboard",
            "fonts" to "appearance",
            "keyboardSkins" to "skin",
            "toolbar" to "toolbar",
            "symbolBar" to "symbol_bar",
            "phrase" to "phrase",
            "clipboard" to "clipboard",
            "rime" to "rime",
            "backup" to "backup",
            "remoteStorage" to "userdict",
        )
        assertEquals(14, expected.size)
        for ((alias, page) in expected) {
            assertEquals(
                "alias $alias",
                page,
                pageOf("hime://com.jobeen.hime/$alias"),
            )
        }
        // 第 15 项 typingPractice 已定不做：不导航。
        assertNull(pageOf("hime://com.jobeen.hime/typingPractice"))
    }

    // ---- 回落 ----

    @Test
    fun `fallback bx0 ids map to pages`() {
        val expected = mapOf(
            "skins" to "skin",
            "keyboardMenu" to "keyboardMenu",
            "files" to "files",
            "ai" to "ai",
        )
        for ((id, page) in expected) {
            assertEquals("fallback $id", page, pageOf("hime://com.jobeen.hime/$id"))
        }
    }

    @Test
    fun `catalog keys route directly`() {
        val keys = listOf(
            "schema", "scheme_update", "rime", "candidates", "skin", "scripts",
            "keyboard", "keyboardMenu", "toolbar", "symbol_bar",
            "skin_editor", "appearance", "clipboard", "phrase",
            "ai", "merged", "voice", "stats", "userdict", "backup", "files",
            "about",
        )
        assertEquals(22, keys.size)
        for (key in keys) {
            assertEquals(key, pageOf("hime://com.jobeen.hime/$key"))
        }
    }

    @Test
    fun `settled not-to-do pages do not navigate`() {
        assertNull(pageOf("hime://com.jobeen.hime/pro"))
        assertNull(pageOf("hime://com.jobeen.hime/keyboardPreview"))
        assertNull(pageOf("hime://com.jobeen.hime/appIcon"))
    }

    @Test
    fun `unknown path does not navigate`() {
        assertNull(pageOf("hime://com.jobeen.hime/noSuchPage"))
    }

    @Test
    fun `phrase id param is carried`() {
        val target = resolve("hime://com.jobeen.hime/phrase?id=123")!!
        assertEquals("phrase", target.pageKey)
        assertEquals("123", target.params["id"])
    }
}
