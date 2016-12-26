package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 * Implements a randomizer consisting of a number of different dice, which can be aggregated by
 * an aggregator object to determine the final value
 */
class DiceCombination(initialRandomizers: List<Randomizer>,
                      val aggregator: Aggregator = SumAggregator()) : Randomizer {

    companion object {
        val LIKELY_PROBABILITY = Probability(1.0 / 1000.0)
    }

    val randomizers = initialRandomizers.sorted()
    override val min = aggregator.min(randomizers)
    override val max = aggregator.max(randomizers)

    override fun mostLikelyValue() = aggregate(randomizers.map { it.mostLikelyValue() })

    /**
     * Returnsa a range that excludes less than minProbability of possible rolls
     */
    override fun range(minProbability: Double): IntRange {
        val values = possibleValues(endCondition = totalProbabilityOf(1.0 - minProbability))
        val min = values.min() ?: 0
        val max = values.max() ?: 0
        return min..max
    }

    /**
     * Returns an end condition that ensures that included rolls have a total probability greater
     * than limit
     */
    fun totalProbabilityOf(limit: Double): (List<Int>) -> Boolean {
        var total = 0.0
        return { values -> total += probabilityOfRolling(values).value; total > limit }
    }

    /**
     * Returns a filter excluding anything less likely than minProbability
     */
    fun moreProbableThan(minimum: Double): (List<Int>) -> Boolean {
        return { values -> probabilityOfRolling(values).value >= minimum }
    }

    override fun roll(): RollResult {
        val values = randomizers.flatMap { it.roll().values }
        return RollResult(values, this)
    }

    fun defaultRange(): IntRange {
        return 1..10
    }

    fun possibleRolls(range: IntRange = defaultRange(),
                      filter: (List<Int>) -> Boolean = { true },
                      endCondition: (List<Int>) -> Boolean = { false }): Iterable<List<Int>> {
        val ranges = aggregator.limitRanges(range, randomizers.map { it.range() })
        return Permutator(ranges, filter = filter, endCondition = endCondition,
                startAt = randomizers.map { it.mostLikelyValue() })
    }

    fun possibleValues(range: IntRange = defaultRange(),
                       filter: (List<Int>) -> Boolean = { true },
                       endCondition: (List<Int>) -> Boolean = { false }): Iterable<Int> {
        return possibleRolls(range, filter = filter, endCondition = endCondition).
                map { aggregate(it) }.distinct()
    }

    fun aggregate(values: List<Int>): Int = aggregator.aggregate(values)

    fun probabilityOfRolling(values: List<Int>): Probability {
        val probs = values.zip(randomizers).map({ pair -> pair.second.probToRoll(pair.first) })
        return Probability.and(probs)
    }

    override fun probToRoll(target: Int): Probability {
        return Probability.sum(
                possibleRolls(defaultRange(), { aggregate(it) == target }).map { probabilityOfRolling(it) })
    }

    override fun probToBeat(target: Int): Probability {
        return Probability.sum(
               possibleRolls(defaultRange(), { aggregate(it) >= target }).map { probabilityOfRolling(it) })
    }

    fun probabilitiesByValue(range: IntRange,
                             filter: (List<Int>) -> Boolean = { true },
                             endCondition: (List<Int>) -> Boolean = { false }) =
            possibleValues(range, filter = filter, endCondition = endCondition).
                    associate { Pair(it, probToRoll(it).value) }

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