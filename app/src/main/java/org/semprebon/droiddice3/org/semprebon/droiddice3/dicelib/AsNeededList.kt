package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Creates a list around an iterator, allowing random access to the early elements of a
 * potentially infinite or just expensive to recompute iterator.
 *
 * The list is backed by a store, a mutable list which is appended to when we need to add
 * items from the iterator
 */
class AsNeededList<T>(val iterator: Iterator<T>) : List<T> {
    val store: MutableList<T> = mutableListOf()

    override fun listIterator(): ListIterator<T> {
        throw UnsupportedOperationException()
    }

    override val size: Int
        get() = throw UnsupportedOperationException()

    fun containsIndex(index: Int): Boolean {
        loadTo(index)
        return store.count() > index
    }

    override fun contains(element: T): Boolean {
        return indexOf(element) == -1
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): T {
        loadTo(index)
        return store.get(index)
    }

    override fun indexOf(element: T): Int {
        val i = store.indexOf(element)
        if (i >= 0) {
            return i
        } else {
            while (iterator.hasNext()) {
                val nextElement = loadNext()
                if (nextElement == element) return store.count()-1
            }
            return -1
        }
    }

    override fun isEmpty(): Boolean {
        return store.isEmpty() && !iterator.hasNext()
    }

    override fun iterator(): Iterator<T> {
        loadAll()
        return store.iterator()
    }

    override fun lastIndexOf(element: T): Int {
        loadAll()
        return store.lastIndexOf(element)
    }

    override fun listIterator(index: Int): ListIterator<T> {
        loadAll()
        return store.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        loadTo(toIndex)
        return store.subList(fromIndex, toIndex)
    }

    private fun loadTo(index: Int) {
        while(iterator.hasNext() && store.count() <= index) {
            loadNext()
        }
    }

    private fun loadAll() {
        while(iterator.hasNext()) {
            loadNext()
        }
    }

    private fun loadNext(): T {
        val element = iterator.next()
        store.add(store.count(), element)
        return element
    }
}
