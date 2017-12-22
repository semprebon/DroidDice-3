package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Assert
import org.junit.Test

/**
 * Test for SumHighestAgregator
 */
class SumLowestAggregatorTest {

    val aggr = SumLowestAggregator(2)
    val dice = listOf(SimpleDie(6), SimpleDie(6), SimpleDie(8))
    val d2 = SimpleDie(2)

    @Test
    fun min() {
        Assert.assertEquals(2, aggr.min(dice))
    }

    @Test
    fun minWithEmptyList() {
        Assert.assertEquals(0, aggr.min(listOf()))
    }

    @Test
    fun max() {
        Assert.assertEquals(12, aggr.max(dice))
    }

    @Test
    fun maxWithEmptyList() {
        Assert.assertEquals(0, aggr.max(listOf()))
    }

    @Test
    fun aggregate() {
        Assert.assertEquals(4, aggr.aggregate(listOf(1, 3, 6)))
    }

    @Test
    fun aggregateWithEmptyList() {
        Assert.assertEquals(0, aggr.aggregate(listOf()))
    }

    @Test
    fun limitRangeToReasonableSizeWith2Pick1() {
        val aggr = SumLowestAggregator(1)
        Assert.assertEquals(listOf(1..2, 1..2),
                aggr.limitRanges(2..2, listOf(1..2, 1..2)))
    }

    @Test
    fun limitRangeToReasonableSize() {
        Assert.assertEquals(listOf(1..100, 2..3, 1..10),
                aggr.limitRanges(2..6, listOf(1..100, 2..3, 1..10)))
    }

    @Test
    fun limitRangesReasonablyWhenNoPossibleLimit() {
        Assert.assertNotNull(aggr.limitRanges(13..100, listOf(1..4, 1..4, 1..4)))
    }

    @Test
    fun probabilitiesForSumLowest() {
        val dice = DiceCombination(listOf(d2, d2), SumLowestAggregator(1))
        val probabilities = dice.probabilitiesByValue(1..2)
        Assert.assertEquals(0.75, probabilities[1])
        Assert.assertEquals(0.25, probabilities[2])
    }
}
