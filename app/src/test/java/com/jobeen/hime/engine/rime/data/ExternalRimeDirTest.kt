// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.engine.rime.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExternalRimeDirTest {
    @Test fun nameValid() { assertTrue(ExternalRimeDir.isNameValid("user")); assertFalse(ExternalRimeDir.isNameValid("")); assertFalse(ExternalRimeDir.isNameValid("../x")) }
    @Test fun switchBadDirFails() { var cur: String? = "/tmp/a"; assertFalse(ExternalRimeDir.switchTransactional(File("/no/such/dir/xyz123"), { cur }, { cur = it })) }
    @Test fun switchGoodDir() { val d = java.nio.file.Files.createTempDirectory("rime").toFile(); var cur: String? = null; assertTrue(ExternalRimeDir.switchTransactional(d, { cur }, { cur = it })); assertEquals(d.canonicalPath, cur) }
    @Test fun offline() { assertTrue(ExternalRimeDir.offlineDisablesNetwork()) }
}
