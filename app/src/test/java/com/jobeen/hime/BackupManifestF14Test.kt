// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime

import com.jobeen.hime.data.backup.BackupManifest
import com.jobeen.hime.data.backup.manifestCounts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupManifestF14Test {
    private fun counts() = manifestCounts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, true)
    @Test fun newKeysDefaultFalse() {
        val m = BackupManifest(2, counts())
        assertEquals(false, m.counts["clipSyncConfigPresent"])
        assertEquals(false, m.counts["voicePresetsPresent"])
        assertEquals(true, m.counts["inputStatsPresent"])
    }
    @Test fun validateCatches() {
        val m = BackupManifest(2, counts() + mapOf("clipSyncConfigPresent" to true))
        val actual = counts() + mapOf("clipSyncConfigPresent" to false)
        val mm = m.validate(actual)
        assertEquals(1, mm.size)
        assertTrue(mm[0].contains("clipSyncConfig"))
    }
    @Test fun keysIncludeF14() {
        assertTrue(BackupManifest.MANIFEST_KEYS.containsAll(listOf("clipSyncConfigPresent", "voicePresetsPresent", "themesDirsPresent")))
    }
}
