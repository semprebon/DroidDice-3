package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Andrew on 11/25/2016.
 */
class SumAggregatorTest {

    val aggr = SumAggregator()
    val dice = listOf(SimpleDie(6), SimpleDie(6))

    @Test
    fun min() {
        assertEquals(2, aggr.min(dice))
    }

    @Test
    fun minWithEmptyList() {
        assertEquals(0, aggr.min(listOf()))
    }

    @Test
    fun max() {
        assertEquals(12, aggr.max(dice))
    }

    @Test
    fun maxWithEmptyList() {
        assertEquals(0, aggr.max(listOf()))
    }

    @Test
    fun aggregate() {
        assertEquals(10, aggr.aggregate(listOf(1,3,6)))
    }

    @Test
    fun aggregateWithEmptyList() {
        assertEquals(0, aggr.aggregate(listOf()))
    }
}