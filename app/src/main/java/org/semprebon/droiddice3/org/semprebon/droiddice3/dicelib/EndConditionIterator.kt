package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * outputs from the specified iterator until a certain condition of the next value is met
 */
class EndConditionIterator<T>(val iterator: Iterator<T>, val condition: (T) -> Boolean) : Iterator<T> {
    private var current: T? = null

    init {
        advance()
    }

    override fun hasNext(): Boolean {
        return current != null
    }

    override fun next(): T {
        val c : T = current!!
        if (condition(c)) {
            current = null
        } else {
            advance()
        }
        return c
    }

    private fun advance() {
        current = null
        if (iterator.hasNext()) {
            current = iterator.next()
        }
    }
}
