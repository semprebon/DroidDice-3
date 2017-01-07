package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Test
import org.junit.Assert.*
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.TestSupport.Companion.ERR

/**
 * Test for DiceCombination
 */
class DiceCombinationTest : TestSupport {
    fun assertEquals(expected: Double, actual: Probability) {
        assertEquals(expected, actual.value, ERR)
    }

    val d6 = SimpleDie(6)
    val d6a = SimpleDie(6)
    val d8 = SimpleDie(8)
    val dx6 = ExplodingDie(6)
    val d2 = SimpleDie(2)

    val singleDie = DiceCombination(listOf(d6), SumAggregator())
    val dice = DiceCombination(listOf(d6, d6), SumAggregator())
    val pick1Of2 = DiceCombination(listOf(d2, d2), SumHighestAggregator(1))

    @Test
    fun possibleRollsForSingleDie() {
        assertSameElements((1..6).map({listOf(it)}), singleDie.possibleRolls())
    }

    @Test
    fun probToRoll() {
        assertEquals(2.0/36.0, dice.probToRoll(3))
        assertEquals(6.0/36.0, dice.probToRoll(7))
    }

    @Test
    fun probToRollWithExplodingDie() {
        assertEquals(2.0/36.0, dice.probToRoll(3))
    }

    @Test
    fun probToBeat() {
        assertEquals(21.0/36.0, dice.probToBeat(7))
    }

    @Test
    fun probToBeatWithExplodingDie() {
        assertEquals(35.0/36.0, dice.probToBeat(3))
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
            assertTrue("$r between 2 and 12", r.value in 2..12)
        }
    }
    
    @Test
    fun probabilitiesWithValuesForSimpleDice() {
        val dice = DiceCombination(listOf(d2,d2))
        val result = dice.probabilitiesByValue(2..4)
        assertEquals(result[2]!!, 0.25, ERR)
        assertEquals(result[3]!!, 0.5, ERR)
        assertEquals(result[4]!!, 0.25, ERR)
    }

    @Test
    fun probabilitiesWithValuesWithEndCondition() {
        val dice = DiceCombination(listOf(d2,d2))
        val result = dice.probabilitiesByValue(endCondition = dice.totalProbabilityOf(0.74))
        assertNotNull(result[3])
        assertEquals(2, result.count())
    }

    @Test
    fun probabilitiesWithValuesWithFilter() {
        val dice = DiceCombination(listOf(d2,d2))
        val result = dice.probabilitiesByValue(filter = { values -> values.sum() <= 3})
        assertNotNull(result[2])
        assertNotNull(result[3])
        assertNull(result[4])
    }

    @Test
    fun probabilitiesWithValuesWithRange() {
        val dice = DiceCombination(listOf(d2,d2))
        val result = dice.probabilitiesByValue(3..5)
        assertNull(result[2])
        assertNotNull(result[3])
        assertNotNull(result[4])
        assertNull(result[5])
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
    fun speedPerformanceOfProbabilitiesByValue() {
        var values: Map<Int, Double>? = null
        val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
        val range = dice.range(0.8)

        values = verifySpeed(0.1, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6), SumAggregator())
            dice.probabilitiesByValue(range,
                    endCondition = dice.totalProbabilityOf(0.90))
            }) as Map<Int, Double>
    }

    @Test
    fun memoryPerformanceOfProbabilitiesByValue() {
        var values: Map<Int, Double>? = null
        val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
        val range = dice.range(0.8)

        values = verifyMemoryUse(30*1024, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6), SumAggregator())
            dice.probabilitiesByValue(range,
                    endCondition = dice.totalProbabilityOf(0.90))
        }) as Map<Int, Double>
    }

    @Test
    fun speedPerformanceOfLikelyRange() {
        var values: IntRange? = null
        values = verifySpeed(0.1, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.range(0.90)
        }) as IntRange
    }

    @Test
    fun memoryPerformanceOfLikelyRange() {
        var values: IntRange? = null
        values = verifyMemoryUse(1024, {
            val dice = DiceCombination(listOf(dx6, dx6, dx6, dx6), SumAggregator())
            dice.range(0.90)
        }) as IntRange
    }

    @Test
    fun expectedValueReturnsResultFromAggregator() {
        assertEquals(2*d6.expectedValue, dice.expectedValue, 0.5)
        assertEquals(7.0/4.0, pick1Of2.expectedValue, 0.5)
    }

    @Test
    fun equalWithEqualValues() {
        assertEquals(DiceCombination(listOf(d6, d6a), SumAggregator()), dice)
    }

    @Test
    fun equalsWithDifferentSize() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumAggregator()), dice)
    }

    @Test
    fun equalsWithDifferentAggregator() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumHighestAggregator(1)), dice)
    }

    @Test
    fun equalsWithDifferentOrder() {
        assertEquals(DiceCombination(listOf(d6, d8), SumAggregator()),
                DiceCombination(listOf(d8, d6a), SumAggregator()))
    }

    @Test
    fun equalsWithNull() {
        assertNotEquals(DiceCombination(listOf(d6, d8), SumAggregator()), null)
    }

}