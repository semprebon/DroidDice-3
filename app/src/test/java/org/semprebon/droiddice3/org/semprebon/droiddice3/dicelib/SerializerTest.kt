package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Test
import org.junit.Assert.*



/**
 */
class SerializerTest : TestSupport {
    val serializer = Serializer()

    val d6 = SimpleDie(6)
    val dx8 = ExplodingDie(8)
    val minus2 = Adjustment(-2)
    val plus1 = Adjustment(1)

    @Test
    fun deserializeSimpleDie() {
        assertEquals(DiceCombination(listOf(d6)), serializer.deserialize("d6"))
    }

    @Test
    fun deserializeExplodingDie() {
        assertEquals(DiceCombination(listOf(dx8)), serializer.deserialize("d8!"))
    }

    @Test
    fun deserializeSeveralDifferentDice() {
        assertEquals(DiceCombination(listOf(dx8, d6, minus2)), serializer.deserialize("d8!+d6-2"))
    }

    @Test
    fun deserializeMultipleDice() {
        assertEquals(DiceCombination(listOf(d6, d6)), serializer.deserialize("2d6"))
    }

    @Test
    fun deserializeSumHighest() { assertEquals(DiceCombination(listOf(d6, d6, d6, d6), SumHighestAggregator(3)),
                serializer.deserialize("4d6[k3]"))
    }

    @Test(expected=Serializer.ParseException::class)
    fun deserializeInvalidDie() {
        serializer.deserialize("du")
    }

    @Test(expected=Serializer.ParseException::class)
    fun deserializeInvalidTerm() {
        serializer.deserialize("d6++")
    }

    @Test
    fun serializeSimpleDie() {
        assertEquals("d6", serializer.serialize(DiceCombination(listOf(d6))))
    }

    @Test
    fun serializeExplodingDie() {
        assertEquals("d8!", serializer.serialize(DiceCombination(listOf(dx8))))
    }

    @Test
    fun serializeSeveralDifferentDice() {
        assertEquals("d6+d8!+-2", serializer.serialize(DiceCombination(listOf(dx8, d6, minus2))))
    }

    @Test
    fun serializeMultipleDice() {
        assertEquals("2d6", serializer.serialize(DiceCombination(listOf(d6, d6))))
    }

    @Test
    fun serializeSumHighest() {
        assertEquals("4d6[k3]",
                serializer.serialize(DiceCombination(listOf(d6, d6, d6, d6), SumHighestAggregator(3))))
    }

    @Test
    fun serializeSumHighestWithDifferentDice() {
        assertEquals("d6+d8![k1]",
                serializer.serialize(DiceCombination(listOf(d6, dx8), SumHighestAggregator(1))))
    }
}