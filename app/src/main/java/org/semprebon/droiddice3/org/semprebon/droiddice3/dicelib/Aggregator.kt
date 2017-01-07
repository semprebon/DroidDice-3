package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * An aggregator determines how the individual ramdomizer rolls are aggregated into a final result
 */
interface Aggregator {
    fun min(randomizers: List<Randomizer>): Int

    fun max(randomizers: List<Randomizer>): Int

    fun aggregate(values: List<Int>): Int

    fun limitRanges(limit: IntRange, ranges: List<IntRange>): List<IntRange>

    fun expectedValue(randomizers: List<Randomizer>): Double {
        return (1..100).map { aggregate(randomizers.map { it.roll().value }) }.average()
    }
}