// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class ModelFetchTest {
    @Test fun allowedHost() { assertTrue(ModelFetch.urlAllowed("https://github.com/Jobeen7/Hime/releases/download/x")) }
    @Test fun evilHostRejected() { assertFalse(ModelFetch.urlAllowed("https://evil.com/x")) }
    @Test fun httpRejected() { assertFalse(ModelFetch.urlAllowed("http://github.com/x")) }
    @Test fun size() { assertTrue(ModelFetch.sizeAllowed(1000L)); assertFalse(ModelFetch.sizeAllowed(0L)) }
    @Test fun zipSlip() { assertFalse(ModelFetch.entrySafe("../evil")); assertTrue(ModelFetch.entrySafe("a/b.tflite")) }
    @Test fun dest() { assertEquals("models/a/b.tflite", ModelFetch.destFor("a/b.tflite")); assertNull(ModelFetch.destFor("../x")) }
}
