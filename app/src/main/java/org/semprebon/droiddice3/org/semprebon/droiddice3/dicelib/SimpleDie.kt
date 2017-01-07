package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Models a simple n-sided die
 */
open class SimpleDie(val sides: Int) : Randomizer {
    override val min = 1
    override val max = sides
    open val size = (max - min) + 1
    val baseProbability = Probability(1.0/sides)

    override val expectedValue by lazy { (min + max) / 2.0 }

    override fun range(minProbability: Double): IntRange {
        if (baseProbability.value > minProbability) {
            return min..max
        } else {
            return min..min
        }
    }

    override fun roll(): RollResult {
        val value = Randomizer.random.nextInt(sides) + 1
        return RollResult(listOf(value), this)
    }

    override fun probToRoll(target: Int): Probability {
        return if (target in min..max) baseProbability else Probability.NEVER
    }

    override fun probToBeat(target: Int): Probability {
        if (target <= min) return Probability.ALWAYS
        if (target > max) return Probability.NEVER
        return Probability((size - (target - min)).toDouble() / size)
    }

    override fun equals(other: Any?) : Boolean {
        if (other !is SimpleDie) return false
        if (other.javaClass != javaClass) return false
        return EqualsBuilder().append(size, other.size).isEquals()
    }

    override fun hashCode() : Int {
        return HashCodeBuilder(3, 5).append(size).toHashCode()
    }

    override fun compareTo(other: Randomizer): Int {
        var builder = compareToBuilder(other)
        if (other is SimpleDie) builder = builder.append(this.size, other.size)
        return builder.toComparison()
    }
}