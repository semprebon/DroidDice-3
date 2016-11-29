package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

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
 */
class Serializer {

    private data class ResultStrings(val diceStr: String, val aggregatorStr: String)

    fun deserialize(s: String): DiceCombination {
        val matcher = Regex("\\[k(\\d+)\\]?\\Z").find(s)
        val aggregator =
            if (matcher != null) {
                val keepStr = matcher.groupValues.component2()
                SumHighestAggregator(keepStr.toInt() ?: 1)
            } else {
                SumAggregator()
            }
        val diceStr = if (matcher != null) s.substring(0, matcher.range.first) else s
        val randomizers = diceStr.split("+").flatMap({ deserializeTerm(it)})
        return DiceCombination(randomizers)
    }

    fun serialize(dice: DiceCombination): String {
        val randomizersString = dice.randomizers.
                groupBy { serializeComponent(it) }.
                map({
                    val prefix = if (it.value.size == 1) "" else it.value.size.toString()
                    "${prefix}${it.key}"
                }).joinToString("+")
        val suffix = when (dice.aggregator) {
                is SumAggregator -> ""
                is SumHighestAggregator -> "[k${dice.aggregator.n}]"
                else -> throw Exception("Unknown aggregator")
            }
        return randomizersString + suffix
    }

    private fun serializeComponent(randomizer: Randomizer): String {
        return when (randomizer) {
                is ExplodingDie -> "d${randomizer.size}!"
                is SimpleDie -> "d${randomizer.size}"
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
            throw ParseException("Invalid term ${s}")
        }
    }

    private fun deserializeRandomizer(s: String): Randomizer {
        val matcher1 = Regex("d(\\d+)\\!").matchEntire(s)
        if (matcher1 != null) {
            return ExplodingDie(matcher1.groupValues.component2().toInt())
        }

        val matcher2 = Regex("d(\\d+)").matchEntire(s)
        if (matcher2 != null) return SimpleDie(matcher2.groupValues.component2().toInt())

        throw ParseException("Unrecognized element: ${s}")
    }

    public class ParseException(s: String) : Exception(s) {}
}