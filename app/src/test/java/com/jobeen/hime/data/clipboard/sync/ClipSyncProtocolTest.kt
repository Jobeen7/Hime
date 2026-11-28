// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.clipboard.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipSyncProtocolTest {
    private fun itm(id: String, text: String, v: Int = 1, kind: ClipKind = ClipKind.TEXT, uri: String = "") = ClipSyncItem(id, kind, text, uri, "dev", v)
    @Test fun deniedSuffix() { assertNull(ClipSyncProtocol.filter(itm("a", "x", kind = ClipKind.FILE, uri = "f.apk"))) }
    @Test fun trunc() { assertEquals(2000, ClipSyncProtocol.filter(itm("a", "x".repeat(3000)))!!.text.length) }
    @Test fun code() { assertEquals("123456", ClipSyncProtocol.extractCode("您的验证码是 123456，请勿泄露")); assertNull(ClipSyncProtocol.extractCode("no code")) }
    @Test fun diff() { val d = ClipSyncProtocol.diff(listOf(itm("a", "old")), listOf(itm("a", "new"), itm("b", "hi"))); assertEquals(2, d.size) }
    @Test fun tabs() { assertTrue(ClipSyncProtocol.visibleIn(ClipTab.ALL, itm("a", "x"), true)); assertEquals(ClipTab.OTHER_DEVICE, ClipSyncProtocol.tabOf(itm("a", "x"), false)) }
}
