package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Filters the output of the specified iterator, only returning those items
 * that meet the specified filter
 */
class FilterIterator<T>(val iterator: Iterator<T>, val condition: (T) -> Boolean) : Iterator<T> {
    var current: T? = null

    init {
        advance()
    }

    override fun hasNext(): Boolean {
        return current != null
    }

    override fun next(): T {
        val c : T = current!!
        advance()
        return c
    }

    private fun advance() {
        current = null
        while (iterator.hasNext()) {
            val c = iterator.next()
            if (condition(c!!)) {
                current = c
                break
            }
        }
    }
}
