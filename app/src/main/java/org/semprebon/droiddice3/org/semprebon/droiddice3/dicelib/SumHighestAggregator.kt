package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * SumHighestAggregator adds up the highest n dice
 */
class SumHighestAggregator(val n: Int) : Aggregator {
    override fun min(randomizers: List<Randomizer>): Int {
        return aggregate(randomizers.map({ it.min }))
    }

    override fun max(randomizers: List<Randomizer>): Int {
        return aggregate(randomizers.map({ it.max }))
    }

    override fun aggregate(values: List<Int>): Int {
        return values.sorted().takeLast(n).fold(0, { a,b -> a+b })
    }

    override fun limitRanges(limit: IntRange, ranges: List<IntRange>): List<IntRange> {
        return ranges.map { range ->
            val minOthers = minOfN(n-1, ranges.minusElement(range))
            val max = Math.min(limit.last - minOthers, range.last)
            val maxOthers = maxOfN(n-1, ranges.minusElement(range))
            val min = Math.max(limit.first - maxOthers, range.first)
            min..max
        }
    }

    private fun minOfN(n: Int, ranges: List<IntRange>): Int {
        return ranges.map { it.first }.sorted().reversed().take(n).sum()
    }

    private fun maxOfN(n: Int, ranges: List<IntRange>): Int {
        return ranges.map { it.last }.sorted().reversed().take(n).sum()
    }
}