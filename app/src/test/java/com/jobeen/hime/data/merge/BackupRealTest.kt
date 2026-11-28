// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
class BackupRealTest {
    @Test fun fileStoreRoundtrip() {
        val dir = Files.createTempDirectory("backup-real").toFile()
        try {
            val store = BackupReal.FileBackupStore(dir)
            val bytes = "hello".toByteArray(Charsets.UTF_8)
            assertTrue(store.write("a.bin", bytes))
            assertArrayEquals(bytes, store.read("a.bin"))
            assertEquals(listOf("a.bin"), store.list())
            assertFalse(store.write("../escape", bytes))
        } finally { dir.deleteRecursively() }
    }
    @Test fun cryptoRoundtrip() {
        val key = ByteArray(32) { it.toByte() }
        val plain = "secret".toByteArray(Charsets.UTF_8)
        val blob = BackupReal.Crypto.encrypt(key, plain)!!
        assertArrayEquals(plain, BackupReal.Crypto.decrypt(key, blob))
    }
    @Test fun cryptoTamperFails() {
        val key = ByteArray(32) { 7 }
        val blob = BackupReal.Crypto.encrypt(key, "abc".toByteArray(Charsets.UTF_8))!!
        val tampered = blob.copyOf()
        tampered[tampered.size - 1] = (tampered[tampered.size - 1].toInt() xor 1).toByte()
        assertNull(BackupReal.Crypto.decrypt(key, tampered))
    }
    @Test fun badKeyRejected() { assertNull(BackupReal.Crypto.encrypt(ByteArray(8), "x".toByteArray(Charsets.UTF_8))) }
}
