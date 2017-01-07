package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * A RollResult consists of the set of randomizer values generated for a specific roll
 */
data class RollResult(val values: List<Int>, val source: Randomizer) {
    val value = when(source) {
        is DiceCombination -> source.aggregator.aggregate(values)
        else -> values[0]
    }
}