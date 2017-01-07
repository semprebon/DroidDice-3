package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import java.util.Random

/**
 * A randomizer generates a random integer value.
 */
interface Randomizer : Comparable<Randomizer> {
    companion object {
        val random = Random()
        const val MIN_PROBABILITY = 0.0000001 // 1 in a million
    }
    val min: Int
    val max: Int
    val expectedValue: Double

    fun range(minProbability: Double = MIN_PROBABILITY): IntRange

    fun roll(): RollResult
    fun probToRoll(target: Int): Probability
    fun probToBeat(target: Int): Probability
    fun probToRollOver(target: Int) = probToBeat(target+1)
    fun probToRollUnder(target: Int) = probToBeat(target).not()

    fun compareToBuilder(other: Randomizer): CompareToBuilder {
        return CompareToBuilder().
                append(this.javaClass.name, other.javaClass.name)
    }
}

