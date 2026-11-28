// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.data.merge
import org.junit.Assert.*
import org.junit.Test
class InferenceRealTest {
    @Test fun readyCanInfer() { assertTrue(InferenceReal.canInfer(InferenceReal.Phase.READY, 100, 0)) }
    @Test fun closedCannotInfer() { assertFalse(InferenceReal.canInfer(InferenceReal.Phase.CLOSED, 100, 0)) }
    @Test fun oversizeRejected() { assertFalse(InferenceReal.canInfer(InferenceReal.Phase.READY, InferenceReal.MAX_INPUT_BYTES + 1, 0)) }
    @Test fun busyRejected() { assertFalse(InferenceReal.canInfer(InferenceReal.Phase.READY, 10, 1)) }
    @Test fun lifecycle() { assertEquals(InferenceReal.Phase.READY, InferenceReal.next(InferenceReal.next(InferenceReal.Phase.CLOSED, true, false), true, false)) }
    @Test fun timeout() { assertTrue(InferenceReal.timedOut(3000L)); assertFalse(InferenceReal.timedOut(10L)) }
}
