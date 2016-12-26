package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import java.util.Random;

/**
 * Created by Andrew on 11/25/2016.
 */
interface Randomizer : Comparable<Randomizer> {
    val min: Int
    val max: Int

    companion object {
        val random = Random()
        val MIN_PROBABILITY = 0.0000001 // 1 in a million
    }

    fun range(minProbability: Double = MIN_PROBABILITY): IntRange

    fun roll(): RollResult
    fun probToRoll(target: Int): Probability
    fun probToBeat(target: Int): Probability
    fun probToRollOver(target: Int) = probToBeat(target+1)
    fun probToRollUnder(target: Int) = probToBeat(target).not()
    fun mostLikelyValue(): Int = (max + min) / 2

    fun compareToBuilder(other: Randomizer): CompareToBuilder {
        return CompareToBuilder().
                append(this.javaClass.name, other.javaClass.name)
    }
}

