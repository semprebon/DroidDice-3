package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by semprebon on 12/24/16.
 */
class PermutatorTest : TestSupport {

    @Test
    fun returnsSinglePermutationFromSingleList() {
        val permutator = Permutator(listOf(2..2))
        assertSameElements(listOf(listOf(2)), permutator)
    }

    @Test
    fun returnsPermutationsFromEmptyList() {
        val permutator = Permutator(listOf(2..2, listOf()))
        assertSameElements(listOf(), permutator)
    }


    @Test
    fun ReturnsAllPermutations() {
        System.err.println("Starting test")
        val permutator = Permutator(listOf(1..3, listOf(5,10)))
        System.err.println("permutator created")
        assertEquals(
            listOf(listOf(1,5), listOf(2,5), listOf(1,10), listOf(3,5), listOf(2,10), listOf(3,10)),
            permutator.map { it })
    }

    @Test
    fun ReturnsPermutationsWithCondition() {
        val permutator = Permutator(listOf(1..3, listOf(5,10)), filter = { list -> list.sum() <= 12 })
        assertEquals(
                listOf(listOf(1,5), listOf(2,5), listOf(1,10), listOf(3,5), listOf(2,10)),
                permutator.map { it })
    }

    @Test
    fun ReturnsPermutationsWithEndCondition() {
        var total = 0
        val permutator = Permutator(listOf(0..3, 0..5), endCondition = { list -> total += list.sum(); total >= 6 })
        assertEquals(
                listOf(listOf(0,0), listOf(1,0), listOf(0,1), listOf(2,0), listOf(1,1)),
                permutator.map { it })
    }

    @Test
    fun ReturnsPermutationsFromStartingValues() {
        var total = 0
        val permutator = Permutator(listOf(0..2, 0..2), startAt = listOf(1,1))
        assertEquals(
                listOf(listOf(1,1),
                        listOf(0,1), listOf(2,1), listOf(1,0), listOf(1,2),
                        listOf(0,0), listOf(0,2), listOf(2,0), listOf(2,2)),
                permutator.map { it })
    }
}