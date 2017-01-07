package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Test
import org.junit.Assert.*
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.TestSupport.Companion.ERR

/**
 * Test for SimpleDie
 */
class SimpleDieTest {
    fun assertProbabilityEqual(expected: Double, actual: Probability) {
        ProbabilityTest.assertNearlyEqual(actual.value, expected)
    }

    val d6 = SimpleDie(6)
    val d8 = SimpleDie(8)

    @Test
    fun probToRoll() {
        assertProbabilityEqual(1.0 / 6.0, d6.probToRoll(2))
        assertProbabilityEqual(0.0, d6.probToRoll(0))
        assertProbabilityEqual(0.0, d6.probToRoll(7))
    }

    @Test
    fun probToBeat() {
        assertProbabilityEqual(4.0 / 6.0, d6.probToBeat(3))
        assertProbabilityEqual(1.0, d6.probToBeat(1))
        assertProbabilityEqual(0.0, d6.probToBeat(7))
    }

    @Test
    fun probToRollOver() {
        assertProbabilityEqual(3.0 / 6.0, d6.probToRollOver(3))
        assertProbabilityEqual(1.0, d6.probToRollOver(0))
        assertProbabilityEqual(0.0, d6.probToRollOver(7))
    }

    @Test
    fun probToRollUnder() {
        assertProbabilityEqual(2.0 / 6.0, d6.probToRollUnder(3))
        assertProbabilityEqual(0.0, d6.probToRollUnder(1))
        assertProbabilityEqual(1.0, d6.probToRollUnder(7))
    }

    @Test
    fun range() { assertEquals(d8.range(), 1..8) }

    @Test
    fun roll() {
        for (i in 1..10) {
            val r = d6.roll().value
            assertTrue("$r is between 1 and 6", r in 1..6)
        }
    }

    @Test
    fun expectedValueIsAverage() {
        assertEquals(3.5, d6.expectedValue, ERR)
    }

    @Test
    fun equalsWithEqualDice() {
        assertEquals(SimpleDie(6), d6)
    }

    @Test
    fun equalsWithDifferentSizeDice() {
        assertNotEquals(SimpleDie(8), d6)
    }

    @Test
    fun equalsWithDifferentTypeDice() {
        assertNotEquals(d6, ExplodingDie(6))
    }

    @Test
    fun equalsWithNull() {
        assertNotEquals(SimpleDie(8), null)
    }

    @Test
    fun equalsWithNonDie() {
        assertNotEquals(SimpleDie(8), 6)
    }

    @Test
    fun hashCodeReturnsDifferentValues() {
        assertEquals(SimpleDie(6).hashCode(), d6.hashCode())
        assertNotEquals(SimpleDie(8), d6.hashCode())
    }

    @Test
    fun compareToWithSameType() {
        assertTrue(SimpleDie(6).compareTo(SimpleDie(8)) != 0)
        assertTrue(SimpleDie(6).compareTo(SimpleDie(6)) == 0)
        assertTrue(SimpleDie(6).compareTo(SimpleDie(4)) != SimpleDie(6).compareTo(SimpleDie(8)))
    }

    @Test
    fun compareToWithDifferentTypes() {
        assertTrue(SimpleDie(6).compareTo(ExplodingDie(6)) != 0)
        assertTrue(ExplodingDie(6).compareTo(SimpleDie(6)) != SimpleDie(6).compareTo(ExplodingDie(6)))
    }
}