package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Created by Andrew on 11/25/2016.
 */
class SumAggregator : Aggregator {
    override fun min(randomizers: List<Randomizer>): Int = aggregate(randomizers.map({ it.min }))
    override fun max(randomizers: List<Randomizer>): Int = aggregate(randomizers.map({ it.max }))
    override fun aggregate(values: List<Int>): Int = values.fold(0, { a,b -> a+b })
}