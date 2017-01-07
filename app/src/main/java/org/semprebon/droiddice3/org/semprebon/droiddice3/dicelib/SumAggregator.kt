package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * SumAggregator simply adds up the individual rolls
 */
class SumAggregator : Aggregator {
    override fun min(randomizers: List<Randomizer>): Int = aggregate(randomizers.map({ it.min }))
    override fun max(randomizers: List<Randomizer>): Int = aggregate(randomizers.map({ it.max }))
    override fun aggregate(values: List<Int>): Int = values.sum()

    override fun limitRanges(limit: IntRange, ranges: List<IntRange>): List<IntRange> {
        val minAll = ranges.fold(0, { sum, r -> sum + r.first })
        val maxAll = ranges.fold(0, { sum, r -> sum + r.last })
        return ranges.map { range ->
            val minOthers = minAll - range.first
            val max = Math.min(limit.last - minOthers, range.last)
            val maxOthers = maxAll - range.last
            val min = Math.max(limit.first - maxOthers, range.first)
            min..max
        }
    }

    override fun expectedValue(randomizers: List<Randomizer>): Double {
        return randomizers.map { it.expectedValue }.sum()
    }
}