package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import java.util.regex.Pattern

/**
 * Converts strings to and from DiceCombinations.
 *
 * Uses a subset of Dicenomicon (http://www.dicenomicon.com/documentation/page9/page9.html) notation
 * * 3d6 - three normal six-sided dice, summed
 * * d8! - exploding d8
 * * d8+d6 - Add d8 and d6
 * * Not yet implemented
 * * * d6+1 - add 1 to d6 roll
 * * * d% - same as d100 or 10*d10+d10
 * * * dF - Fudge dice
 * * * 3d6>5 - count rolls > 5
 * * * 3d6K2 or 3d6H2 - roll 3 d6 and sum  highest 2
 * * * 3d6L2 - roll 3 d6 and sum lowest 2
 *
 *  TODO: Use dx-y in dice specs instead of dx+-y
 */
class Serializer {

    fun deserialize(s: String): DiceCombination {
        val matcher = Regex("\\[k(\\d+)\\]?\\Z").find(s)
        val aggregator =
            if (matcher != null) {
                val keepStr = matcher.groupValues.component2()
                SumHighestAggregator(keepStr.toInt())
            } else {
                SumAggregator()
            }
        val diceStr = (if (matcher != null) s.substring(0, matcher.range.first) else s).
                replace("-", "+-")
        val termRegex = Pattern.compile("(.+?)(?:\\+|-|\\+-)")
        val termMatcher = termRegex.matcher(diceStr)
        val randomizers: MutableList<Randomizer> = mutableListOf()
        var endMatch = 0
        while (termMatcher.lookingAt()) {
            val operator = if (termMatcher.groupCount() == 2) termMatcher.group(2) else null
            val term = (if (operator == "-1") "-" else "") + termMatcher.group(1)
            randomizers.addAll(deserializeTerm(term))
            endMatch = termMatcher.end()
            termMatcher.region(endMatch, diceStr.length)
        }
        randomizers.addAll(deserializeTerm(diceStr.substring(endMatch)))
        return DiceCombination(randomizers, aggregator)
    }

    fun serialize(dice: DiceCombination): String {
        val randomizersString = dice.randomizers.
                groupBy { serializeRandomizer(it) }.
                map({
                    val prefix = if (it.value.size == 1) "" else it.value.size.toString()
                    "$prefix${it.key}"
                }).joinToString("+")
        val suffix = when (dice.aggregator) {
                is SumAggregator -> ""
                is SumHighestAggregator -> "[k${dice.aggregator.n}]"
                else -> throw Exception("Unknown aggregator")
            }
        return randomizersString + suffix
    }

    fun serializeRandomizer(randomizer: Randomizer): String {
        return when (randomizer) {
                is ExplodingDie -> "d${randomizer.size}!"
                is SimpleDie -> "d${randomizer.size}"
                is Adjustment -> randomizer.value.toString()
                else -> "?"
            }
    }

    private fun deserializeTerm(s: String): List<Randomizer> {
        val matcher = Regex("(\\d*)(.+)").matchEntire(s)
        if (matcher != null) {
            val (multiplierString, randomizer) = matcher.destructured
            val multiplier = if (multiplierString.isBlank()) 1 else multiplierString.toInt()
            return (1..multiplier).map({ deserializeRandomizer(randomizer) })
        } else {
            throw ParseException("Invalid term $s")
        }
    }

    private fun deserializeRandomizer(s: String): Randomizer {
        val matcher1 = Regex("d(\\d+)!").matchEntire(s)
        if (matcher1 != null) {
            return ExplodingDie(matcher1.groupValues.component2().toInt())
        }

        val matcher2 = Regex("d(\\d+)").matchEntire(s)
        if (matcher2 != null) return SimpleDie(matcher2.groupValues.component2().toInt())

        val matcher3 = Regex("-?\\d+").matchEntire(s)
        if (matcher3 != null) {
            return Adjustment(s.toInt())
        }
        throw ParseException("Unrecognized element: $s")
    }

    class ParseException(s: String) : Exception(s)
}