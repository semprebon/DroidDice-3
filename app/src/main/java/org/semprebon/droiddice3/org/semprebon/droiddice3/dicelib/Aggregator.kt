package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

/**
 * Created by Andrew on 11/25/2016.
 */
interface Aggregator {
    fun min(randomizers: List<Randomizer>): Int
    fun max(randomizers: List<Randomizer>): Int
    fun aggregate(values: List<Int>): Int
}