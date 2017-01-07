package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import java.text.DecimalFormat

/**
 * Basic probability math
 */
class Probability(val value: Double) : Comparable<Probability> {

    fun not(): Probability = Probability(1.0 - value)
    infix fun and(p: Probability): Probability = Probability(value * p.value)

    infix fun or(p: Probability) = (not() and p.not()).not()
    infix fun sum(p: Probability): Probability = Probability(value + p.value)

    fun times(n: Int): Probability = Probability(Math.pow(value, n.toDouble()))

    override fun toString(): String {
        return STRING_FORMAT.format(value)
    }

    override fun compareTo(other: Probability): Int {
        return this.value.compareTo(other.value)
    }

    companion object {
        val STRING_FORMAT = DecimalFormat("#0.0000%")
        val NEVER = Probability(0.0)
        val ALWAYS = Probability(1.0)

        fun and(ps: List<Probability>): Probability = ps.fold(ALWAYS, Probability::and)
        fun or(ps: List<Probability>): Probability = and(ps.map(Probability::not)).not()
        fun sum(ps: List<Probability>): Probability = ps.fold(NEVER, Probability::sum)
    }
}
