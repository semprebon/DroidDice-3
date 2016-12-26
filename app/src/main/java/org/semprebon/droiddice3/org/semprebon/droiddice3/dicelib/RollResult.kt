package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Created by Andrew on 11/27/2016.
 */
data class RollResult(val values: List<Int>, val source: Randomizer) {
    val value = when(source) {
        is DiceCombination -> source.aggregator.aggregate(values)
        else -> values[0]
    }
}