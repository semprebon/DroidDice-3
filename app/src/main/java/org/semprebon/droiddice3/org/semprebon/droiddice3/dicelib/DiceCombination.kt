package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 * Created by Andrew on 11/25/2016.
 */
class DiceCombination(val initialRandomizers: List<Randomizer>,
                      val aggregator: Aggregator = SumAggregator()) : Randomizer {

    val randomizers = initialRandomizers.sorted()
    override val min = aggregator.min(randomizers)
    override val max = aggregator.max(randomizers)

    override fun roll(): RollResult {
        val values = randomizers.flatMap { it.roll().values }
        return RollResult(values, this)
    }

    fun permutations(ranges: List<IntRange>) : List<List<Int>> {
        val last = ranges.last()
        if (ranges.size == 1) return last.map({listOf(it)})

        val rest = ranges.dropLast(1)
        return permutations(rest).flatMap({ vs -> last.map({ v -> vs.plus(v) }) })
    }

    val possibleRolls = permutations(randomizers.map { it.range })
    val possibleValues = possibleRolls.map { aggregator.aggregate(it) }.distinct()

    override val range =
            possibleValues.reduce({ a,b -> if (a < b) a else b })..
            possibleValues.reduce({ a,b -> if (a > b) a else b })

    override fun aggregate(values: List<Int>): Int = aggregator.aggregate(values)

    fun probabilityOfRolling(values: List<Int>): Probability {
        val probs = values.zip(randomizers).map({ pair -> pair.second.probToRoll(pair.first) })
        return Probability.and(probs)
    }

    override fun probToRoll(target: Int): Probability {
        return Probability.sum(
                possibleRolls.
                        filter({ roll -> (aggregator.aggregate(roll) == target) }).
                        map({ roll -> probabilityOfRolling(roll)}))
    }

    override fun probToBeat(target: Int): Probability {
        return Probability.sum(
               possibleRolls.
                       filter({ roll -> (aggregator.aggregate(roll) >= target) }).
                       map({ roll -> probabilityOfRolling(roll)}))
    }

    fun valuesByRoll() = range.associate { Pair(it, probToRoll(it).value) }

    override fun equals(other: Any?): Boolean {
        if (other !is DiceCombination) return false
        if (other.javaClass != javaClass) return false
        return EqualsBuilder().
               append(randomizers, other.randomizers).
               isEquals()
    }

    override fun compareTo(other: Randomizer): Int {
        var builder = compareToBuilder(other)
        if (other is DiceCombination) {
            builder.append(randomizers.toTypedArray(), other.randomizers.toTypedArray())
        }
        return builder.toComparison()
    }
}