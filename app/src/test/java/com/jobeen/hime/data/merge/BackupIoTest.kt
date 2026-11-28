// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class BackupIoTest {
    private fun manifest() = UnifiedBackup.build(listOf(UnifiedBackup.Entry("a.txt", 3, "0000000a"), UnifiedBackup.Entry("b.txt", 4, "0000000b")))!!
    @Test fun roundtrip() { val s = BackupIo.MemoryStore(); assertTrue(BackupIo.pack(manifest(), s)); assertEquals(2, BackupIo.unpack(s)?.entries?.size) }
    @Test fun digestMismatch() { val s = BackupIo.MemoryStore(); BackupIo.pack(manifest(), s); s.write("manifest.txt", "x".toByteArray()); assertNull(BackupIo.unpack(s)) }
    @Test fun cryptoPolicy() { assertTrue(BackupIo.validCrypto(32, 12, 128)); assertFalse(BackupIo.validCrypto(32, 16, 128)); assertFalse(BackupIo.validCrypto(8, 12, 128)) }
}
