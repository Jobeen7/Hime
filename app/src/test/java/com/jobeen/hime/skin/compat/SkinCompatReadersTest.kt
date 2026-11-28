// SPDX-License-Identifier: GPL-3.0-only
package com.jobeen.hime.skin.compat

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SkinCompatReadersTest {

    @Test fun trimeYamlParses() {
        val yaml = "name: TestTheme\nbackground: bg.png\naccent: \"#FF0000\"\nkeys:\n- q: q\n- w: w\n"
        val layout = TrimeYamlReader.parse(yaml, "t1")
        assertNotNull(layout)
        assertEquals("TestTheme", layout!!.name)
        assertEquals("bg.png", layout.style.backgroundPath)
        assertEquals(1, layout.rows.size)
        assertEquals("q", layout.rows[0][0].label)
        val model = CompatLayoutMapper.toLayoutModel(layout)
        assertEquals(2, model.keyCount)
        assertEquals("t1", model.layoutId)
    }

    @Test fun trimeYamlBlankReturnsNull() {
        assertNull(TrimeYamlReader.parse("  ", "t2"))
        assertNull(TrimeYamlReader.parse("name: x\n", "t2"))
    }

    @Test fun bdsListsZipEntries() {
        val bytes = makeBds()
        val listing = BaiduBdsReader.parse(bytes, "bds1")
        assertNotNull(listing)
        assertTrue(listing!!.entries.any { it.endsWith("keys.txt") })
        assertTrue(listing.entries.any { it.endsWith(".png", true) })
        assertNotNull(listing.layout)
    }

    @Test fun bdsEmptyReturnsNull() {
        assertNull(BaiduBdsReader.parse(byteArrayOf(), "bds2"))
        assertNull(BaiduBdsReader.parse(byteArrayOf(1, 2, 3), "bds2"))
    }

    @Test fun songyanJsonParses() {
        val json = """{"name":"SY","background":"b.png","rows":[{"keys":[{"label":"A","text":"a","width":2},{"label":"B","text":"b"}]}]}"""
        val tree = Json.parseToJsonElement(json) as JsonObject
        val layout = SongyanLayoutReader.parse(tree, "sy1")
        assertNotNull(layout)
        assertEquals("SY", layout!!.name)
        assertEquals(2, layout.rows[0].size)
        assertEquals(2.0, layout.rows[0][0].widthWeight, 0.001)
        val model = CompatLayoutMapper.toLayoutModel(layout)
        assertEquals(2, model.keyCount)
    }

    @Test fun songyanMissingRowsReturnsNull() {
        val tree = Json.parseToJsonElement("""{"name":"X"}""") as JsonObject
        assertNull(SongyanLayoutReader.parse(tree, "sy2"))
    }

    private fun makeBds(): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            zip.putNextEntry(ZipEntry("bg.png"))
            zip.write(ByteArray(16) { 1 })
            zip.closeEntry()
            zip.putNextEntry(ZipEntry("keys.txt"))
            zip.write("A\nB\nC\n".toByteArray())
            zip.closeEntry()
        }
        return out.toByteArray()
    }
}
