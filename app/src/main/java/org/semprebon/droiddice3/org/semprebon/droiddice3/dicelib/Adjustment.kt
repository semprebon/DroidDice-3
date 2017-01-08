package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import android.media.RemoteController
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 * Implenents a "Randomizer" that just returns the same value
 */
class Adjustment(val value: Int): Randomizer  {
    override val min = value
    override val max = value
    override val expectedValue = value.toDouble()

    override fun range(minProbability: Double) = value..value

    override fun roll(): RollResult = RollResult(listOf(value), this)

    override fun probToRoll(target: Int) = if (target == value) Probability.ALWAYS else Probability.NEVER

    override fun probToBeat(target: Int) = if (target <= value) Probability.ALWAYS else Probability.NEVER

    override fun equals(other: Any?) : Boolean {
        if (other !is Adjustment) return false
        if (other.javaClass != javaClass) return false
        return EqualsBuilder().append(value, other.value).isEquals()
    }

}