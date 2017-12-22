package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * SumHighestAggregator adds up the highest n dice
 */
class SumLowestAggregator(val n: Int) : Aggregator {
    override fun min(randomizers: List<Randomizer>): Int {
        return aggregate(randomizers.map({ it.min }))
    }

    override fun max(randomizers: List<Randomizer>): Int {
        return aggregate(randomizers.map({ it.max }))
    }

    override fun aggregate(values: List<Int>): Int {
        return values.sorted().take(n).fold(0, { a,b -> a+b })
    }

    /**
     * Computes the range of each die given a desired output range.
     *
     * In this case, if the max value of the rest of the dice are minOthers, then any value of
     * of the considered die greater than or equal to that value needn't be considered
     */
    override fun limitRanges(limit: IntRange, ranges: List<IntRange>): List<IntRange> {
        return ranges
    }

    private fun minOfN(n: Int, ranges: List<IntRange>): Int? {
        return ranges.map { it.first }.sorted().reversed().take(n).sum()
    }

    private fun maxOfN(n: Int, ranges: List<IntRange>): Int? {
        val reversed = ranges.map { it.last }.sorted().reversed()
        val take = reversed.take(n)
        return take.sum()
    }
}