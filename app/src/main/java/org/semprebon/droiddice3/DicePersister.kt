package org.semprebon.droiddice3

import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Serializer
import java.io.*

/**
 * Persists the collection of dice to a file
 */
class DicePersister {
    val serializer = Serializer()

    fun loadDice(stream: InputStream): MutableMap<String, DiceCombination>? {
        val input = ObjectInputStream(stream)
        val result: MutableMap<String, DiceCombination> = mutableMapOf()
        do {
            val pair = input.readObject() as Pair<String, String>?
            if (pair != null) {
                result.put(pair.first, serializer.deserialize(pair.second))
            }
        } while (pair != null)
        return if (result.isEmpty()) null else result
    }

    fun loadDice(file: File): MutableMap<String, DiceCombination>? {
        return if (file.exists()) loadDice(FileInputStream(file)) else null
    }

    fun saveDice(stream: OutputStream, diceSet: Map<String, DiceCombination>) {
        val output = ObjectOutputStream(stream)
        for ((name, dice) in diceSet) {
            output.writeObject(Pair(name, serializer.serialize(dice)))
        }
        output.writeObject(null)
        output.close()
    }

    fun saveDice(file: File, diceSet: Map<String, DiceCombination>) {
        saveDice(FileOutputStream(file), diceSet)
    }

}