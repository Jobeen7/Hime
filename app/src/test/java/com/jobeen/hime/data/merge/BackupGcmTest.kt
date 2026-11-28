// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test

class BackupGcmTest {
    private val key = ByteArray(32) { (it + 1).toByte() }

    @Test fun roundtrip() {
        val plain = "hello backup".toByteArray(Charsets.UTF_8)
        val enc = BackupGcm.encrypt(key, plain)!!
        assertTrue(enc.size == BackupGcm.NONCE_LEN + plain.size + 16)
        assertArrayEquals(plain, BackupGcm.decrypt(key, enc))
    }

    @Test fun textRoundtrip() {
        val s = BackupGcm.encryptText(key, "backup-text-123")!!
        assertEquals("backup-text-123", BackupGcm.decryptText(key, s))
    }

    @Test fun nonceRandomized() {
        val plain = "same".toByteArray(Charsets.UTF_8)
        val a = BackupGcm.encrypt(key, plain)!!
        val b = BackupGcm.encrypt(key, plain)!!
        assertFalse(a.contentEquals(b))
        assertArrayEquals(plain, BackupGcm.decrypt(key, a))
        assertArrayEquals(plain, BackupGcm.decrypt(key, b))
    }

    @Test fun tamperFails() {
        val enc = BackupGcm.encrypt(key, "data".toByteArray(Charsets.UTF_8))!!.copyOf()
        enc[enc.size - 1] = (enc[enc.size - 1] + 1).toByte()
        assertNull(BackupGcm.decrypt(key, enc))
    }

    @Test fun wrongKeyFails() {
        val enc = BackupGcm.encrypt(key, "data".toByteArray(Charsets.UTF_8))!!
        assertNull(BackupGcm.decrypt(ByteArray(32) { 9 }, enc))
    }

    @Test fun badKeyLen() {
        assertFalse(BackupGcm.validKey(8))
        assertTrue(BackupGcm.validKey(16))
        assertNull(BackupGcm.encrypt(ByteArray(8), "x".toByteArray()))
    }

    @Test fun shortPayloadNull() {
        assertNull(BackupGcm.decrypt(key, ByteArray(10)))
    }
}
