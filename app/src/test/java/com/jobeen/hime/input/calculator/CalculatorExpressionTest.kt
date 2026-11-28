// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.input.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorExpressionTest {
    @Test fun simple() { assertEquals(80.0, CalculatorExpression.evaluate("12+34*2=")!!, 1e-9) }
    @Test fun paren() { assertEquals(92.0, CalculatorExpression.evaluate("(12+34)*2")!!, 1e-9) }
    @Test fun divide() { assertEquals(2.5, CalculatorExpression.evaluate("10/4")!!, 1e-9) }
    @Test fun negative() { assertEquals(-3.0, CalculatorExpression.evaluate("5-8")!!, 1e-9) }
    @Test fun decimal() { assertEquals(0.5, CalculatorExpression.evaluate("0.25+0.25")!!, 1e-9) }
    @Test fun divZeroNull() { assertNull(CalculatorExpression.evaluate("1/0")); assertNull(CalculatorExpression.evaluate("1/(2-2)")) }
    @Test fun invalidNull() { assertNull(CalculatorExpression.evaluate("12++")); assertNull(CalculatorExpression.evaluate("abc")); assertNull(CalculatorExpression.evaluate("(12")); assertNull(CalculatorExpression.evaluate("")) }
    @Test fun candidateFormat() { assertEquals("80", CalculatorExpression.resolveCandidate("12+34*2=")); assertEquals("2.5", CalculatorExpression.resolveCandidate("10/4")) }
    @Test fun likely() { assertTrue(CalculatorExpression.isLikelyExpression("12+34*2=")); assertTrue(!CalculatorExpression.isLikelyExpression("hello")) }
}
