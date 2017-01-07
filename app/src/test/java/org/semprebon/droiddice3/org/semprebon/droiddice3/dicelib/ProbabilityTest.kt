package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Test
import org.junit.Assert.*

/**
 * Test for Probability
 */
class ProbabilityTest {
    companion object {
        fun assertNearlyEqual(expected: Double, actual: Double) {
            assertTrue("$actual should equal $expected", Math.abs(actual - expected) < 0.00001)
        }
    }

    val p = Probability(0.3)
    val q = Probability(0.2)

    @Test
    fun andWithValues() {
        assertNearlyEqual(0.06, p.and(q).value)
    }

    @Test
    fun or() {
        assertNearlyEqual(0.44, p.or(q).value)
    }

    @Test
    fun not() {
        assertNearlyEqual(0.7, p.not().value)
    }

    @Test
    fun sum() {
        assertNearlyEqual(0.5, p.sum(q).value)
    }

    @Test
    fun times() {
        assertNearlyEqual(0.09, p.times(2).value)
    }

    @Test
    fun timesWithZero() {
        assertNearlyEqual(1.0, p.times(0).value)
    }

    @Test
    fun andWithList() {
        assertNearlyEqual(0.06, Probability.and(listOf(p, q)).value)
    }

    @Test
    fun orWithList() {
        assertNearlyEqual(0.44, Probability.or(listOf(p, q)).value)
    }

    @Test
    fun sumWithList() {
        assertNearlyEqual(0.5, Probability.sum(listOf(p, q)).value)
    }

    @Test
    fun toStringIsUseful() {
        assertEquals("30.0000%", p.toString())
    }
}