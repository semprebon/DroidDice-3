package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import android.util.Log
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Models an exploding die where, if the max value is rolled, the die is rolled
 * again and added
 */
class ExplodingDie(sides: Int) : SimpleDie(sides) {
    val max_rolls = 10
    override val size = sides
    override val min = 1
    override val max = max_rolls * sides
    override val range = 1..(size * max_rolls)

    override fun roll(): RollResult {
        var value = super.roll().value
        var count = 1
        while (value == size && count < 10) {
            count += 1
            value = super.roll().value
        }
        return RollResult(listOf(size*(count-1) + value), this)
    }

    override fun probToRoll(target: Int): Probability {
        if (target < min) return Probability.NEVER
        if (target < size) return baseProbability
        if (target == size) return Probability.NEVER
        return baseProbability.and(probToRoll(target - size))
    }

    override fun probToBeat(target: Int): Probability {
        if (target <= size) return super.probToBeat(target)
        return baseProbability.and(probToBeat(target - size))
    }

    override fun hashCode() : Int {
        return HashCodeBuilder(7, 11).append(size).toHashCode()
    }
}