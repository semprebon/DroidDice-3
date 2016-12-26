package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * Created by Andrew on 11/25/2016.
 */
class DiceCombinationTest : TestSupport {
    fun assertProbabilityEqual(expected: Double, actual: Probability) {
        ProbabilityTest.assertNearlyEqual(expected, actual.value)
    }

    val d6 = SimpleDie(6)
    val d6a = SimpleDie(6)
    val d8 = SimpleDie(8)
    val dx6 = ExplodingDie(6)

    val singleDie = DiceCombination(listOf(d6), SumAggregator())
    val dice = DiceCombination(listOf(d6, d6), SumAggregator())

    @Test
    fun possibleRollsForSingleDie() {
        assertSameElements((1..6).map({listOf(it)}), singleDie.possibleRolls())
    }

    @Test
    fun probToRoll() {
        assertProbabilityEqual(2.0/36.0, dice.probToRoll(3))
        assertProbabilityEqual(6.0/36.0, dice.probToRoll(7))
    }

    @Test
    fun probToRollWithExplodingDie() {
        assertProbabilityEqual(2.0/36.0, dice.probToRoll(3))
    }

    @Test
    fun probToBeat() {
        assertProbabilityEqual(21.0/36.0, dice.probToBeat(7))
    }

    @Test
    fun probToBeatWithExplodingDie() {
        assertProbabilityEqual(35.0/36.0, dice.probToBeat(3))
    }

    @Test
    fun rangeWithSimpleDie() {
        assertEquals(1..6, singleDie.range())
    }

    @Test
    fun rangeWithSeveralDice() {
        assertEquals(2..12, dice.range())
    }

    @Test
    fun roll() {
        for (i in 1..10) {
            val r = dice.roll()
            assertTrue("${r} between 2 and 12", r.value in 2..12)
        }
    }

    @Test
    fun equalWithEqualValues() {
        assertEquals(DiceCombination(listOf(d6, d6a), SumAggregator()), dice)
    }

    @Test
    fun equalsWithDifferetSize() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumAggregator()), dice)
    }

    @Test
    fun equalsWithDifferetAggregator() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumHighestAggregator(1)), dice)
    }

    @Test
    fun equalsWithDifferetOrder() {
        assertEquals(DiceCombination(listOf(d6, d8), SumAggregator()),
                DiceCombination(listOf(d8, d6a), SumAggregator()))
    }

    @Test
    fun equalsWithNull() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumAggregator()), null)
    }

    @Test
    fun compareToWithSameClass() {
        assertTrue(DiceCombination(listOf(d6, d8)).compareTo(
                DiceCombination(listOf(d6, d8))) == 0)
        assertTrue(DiceCombination(listOf(d6, d8)).compareTo(
                DiceCombination(listOf(d6, d6))) != 0)
        assertTrue(DiceCombination(listOf(d6, d8)).compareTo(
                DiceCombination(listOf(d6, d6), SumHighestAggregator(1))) != 0)
    }

    @Test
    fun performanceOfprobabilitiesByValue() {
        var values: Map<Int, Double>? = null
        val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
        val range = dice.range()

        values = verifyMemoryUse(20*1024, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.probabilitiesByValue(dice.range(0.01),
                    endCondition = dice.totalProbabilityOf(0.99))
            }) as Map<Int, Double>
        System.out.println("Permutations: ${values.count()}")

        values = verifySpeed(0.1, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.probabilitiesByValue(dice.range(0.01),
                    endCondition = dice.totalProbabilityOf(0.99))
            }) as Map<Int, Double>
        System.out.println(values[4])
    }

    @Test
    fun performanceOfLikelyRange() {
        var values: IntRange? = null
        values = verifyMemoryUse(1024, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.range()
        }) as IntRange
        values = verifySpeed(0.1, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.range()
        }) as IntRange
    }
}