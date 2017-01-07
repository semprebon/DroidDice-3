package org.semprebon.droiddice3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.SimpleDie
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.ObjectOutputStream

/**
 * Tests DicePersistor
 */
class DicePersistorTest {
    val persistor = DicePersister()

    fun inputStreamWith(diceSets: Map<String, String>): InputStream {
        val stream = ByteArrayOutputStream()
        val output = ObjectOutputStream(stream)
        for (entry in diceSets) {
            output.writeObject(entry.toPair())
        }
        output.writeObject(null)
        return ByteArrayInputStream(stream.toByteArray())
    }

    @Test
    fun loadDice() {
        val diceSets = persistor.loadDice(inputStreamWith(mapOf(Pair("test", "d6"))))!!
        assertEquals(DiceCombination(listOf(SimpleDie(6))), diceSets["test"])
    }

    @Test
    fun loadDiceWithEmptyFile() {
        val diceSets = persistor.loadDice(inputStreamWith(mapOf()))
        assertNull(diceSets)
    }

    @Test
    fun saveDice() {
        val diceSets = mapOf(
                Pair("attack", DiceCombination(listOf(SimpleDie(20)))),
                Pair("damage", DiceCombination(listOf(SimpleDie(6), SimpleDie(6)))))
        val buffer = ByteArrayOutputStream()
        persistor.saveDice(buffer, diceSets)
        val resultDiceSets = persistor.loadDice(ByteArrayInputStream(buffer.toByteArray()))!!
        assertEquals(DiceCombination(listOf(SimpleDie(6), SimpleDie(6))), resultDiceSets["damage"])
    }
}
