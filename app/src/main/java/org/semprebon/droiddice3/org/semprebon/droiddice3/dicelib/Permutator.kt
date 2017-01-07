package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Given a set of ranges (or other iterables), it will generate permutations of those iterables,
 * starting with the lowest valued ones first. For example:
 *
 * Permutator(1..2,1..3) == ((1,1),(1,2),(2,1),(1,3),(2,3))
 *
 * This is designed to work even with unbounded iterators. Thus, you can pass in a filter.
 * Once this filter is met, the permutator stops
 */
class Permutator(val ranges: List<Iterable<Int>>,
                 val startAt: List<Int> = ranges.map { if (it.iterator().hasNext()) it.first() else 0 },
                 val filter: (List<Int>) -> Boolean = { true },
                 val endCondition: (List<Int>) -> Boolean = { false }) :  Iterable<List<Int>> {

    override fun iterator(): Iterator<List<Int>> {
        return EndConditionIterator(FilterIterator(PermutationIterator(), filter), endCondition)
    }

    inner class PermutationIterator : Iterator<List<Int>> {
        val values = ranges.map { AsNeededList(it.iterator()) }
        val queue: MutableList<List<Int>> = mutableListOf()
        val visited: MutableSet<List<Int>> = mutableSetOf()

        init {
            if (ranges.all { it.iterator().hasNext() }) {
                //System.err.println("StartAt = ${startAt}")
                val permutation = startAt.mapIndexed { i, value -> values[i].indexOf(value) }
                enqueue(permutation)
                visited.add(permutation)
            }
        }

        override fun hasNext(): Boolean {
            return !queue.isEmpty()
        }

        override fun next(): List<Int> {
            val currentIndexes = dequeue()
            //System.err.println("Processing ${currentIndexes}")
            val currentValues = values.mapIndexed { i,vs -> vs[currentIndexes[i]] }
            val permutations = permutationsOf(currentIndexes)
            permutations.filterNot { visited.contains(it) }.forEach { enqueue(it) }
            visited.addAll(permutations)
            return currentValues
        }

        private fun permutationsOf(currentIndexes: List<Int>): List<List<Int>> {
            val permutations: MutableList<List<Int>> = mutableListOf()
            currentIndexes.forEachIndexed { i, index ->
                if (index > 0) permutations.add(decrementAt(i, currentIndexes))
                if (values[i].containsIndex(index+1)) permutations.add(incrementAt(i, currentIndexes))
            }
            return permutations
        }

        private fun incrementAt(index: Int, permutation: List<Int>): List<Int> =
            permutation.mapIndexed { i, p -> if (i == index) p+1 else p }

        private fun decrementAt(index: Int, permutation: List<Int>): List<Int> =
                permutation.mapIndexed { i, p -> if (i == index) p-1 else p }

        private fun dequeue() : List<Int> {
            return queue.removeAt(0)
        }

        private fun enqueue(permutation: List<Int>) {
            //System.err.println("  Enq ${permutation} on ${queue.count()}")
            queue.add(queue.count(), permutation)
        }
    }
}

