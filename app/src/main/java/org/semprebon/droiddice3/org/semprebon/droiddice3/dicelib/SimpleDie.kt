package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Models a simple n-sided die
 */
open class SimpleDie(sides: Int) : Randomizer {
    override val min = 1
    override val max = sides
    open val size = (max - min) + 1
    override val range = min..max
    val baseProbability = Probability(1.0/sides)

    var value = min

    override fun roll() = RollResult(listOf(Randomizer.random.nextInt(max - min + 1) + min), this)

    override fun probToRoll(target: Int): Probability {
        return if (target in range) baseProbability else Probability.NEVER
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