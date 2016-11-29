package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.apache.commons.lang3.builder.CompareToBuilder
import java.util.Random;

/**
 * Created by Andrew on 11/25/2016.
 */
interface Randomizer : Comparable<Randomizer> {
    val min: Int
    val max: Int
    val range: IntRange

    companion object {
        val random = Random()
    }

    fun roll(): RollResult
    fun aggregate(values: List<Int>): Int =
            if (values.size == 1) values.first()
            else throw IllegalArgumentException("Expected 1 value, got ${values.joinToString(",")}")
    fun probToRoll(target: Int): Probability
    fun probToBeat(target: Int): Probability
    fun probToRollOver(target: Int) = probToBeat(target+1)
    fun probToRollUnder(target: Int) = probToBeat(target).not()

    fun compareToBuilder(other: Randomizer): CompareToBuilder {
        return CompareToBuilder().
                append(this.javaClass.name, other.javaClass.name)
    }
}

