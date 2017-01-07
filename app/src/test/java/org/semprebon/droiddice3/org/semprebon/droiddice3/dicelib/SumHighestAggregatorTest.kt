package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Assert
import org.junit.Test

/**
 * Test for SumHighestAgregator
 */
class SumHighestAggregatorTest {

    val aggr = SumHighestAggregator(2)
    val dice = listOf(SimpleDie(6), SimpleDie(6), SimpleDie(8))

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
        Assert.assertEquals(14, aggr.max(dice))
    }

    @Test
    fun maxWithEmptyList() {
        Assert.assertEquals(0, aggr.max(listOf()))
    }

    @Test
    fun aggregate() {
        Assert.assertEquals(9, aggr.aggregate(listOf(1, 3, 6)))
    }

    @Test
    fun aggregateWithEmptyList() {
        Assert.assertEquals(0, aggr.aggregate(listOf()))
    }

    @Test
    fun limitRangeToReasonableSize() {
        Assert.assertEquals(listOf(1..4, 2..3, 1..4),
                aggr.limitRanges(2..6, listOf(1..100, 2..3, 1..10)))
    }
    @Test
    fun limitRangesReasonablyWhenNoPossibleLimit() {
        Assert.assertNotNull(aggr.limitRanges(13..100, listOf(1..4,1..4,1..4)))
    }
}