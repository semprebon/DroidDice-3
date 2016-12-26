package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Assert
import java.util.*

/**
 * Created by Andrew on 11/26/2016.
 */
interface TestSupport {
    fun canonize(list: Iterable<Iterable<Int>>): Map<List<Int>, Int> {
        return list.map { it.toList() }
                .fold(HashMap<List<Int>, Int>(),
                    { result, list ->
                        val oldValue = result[list]
                        if (oldValue == null) {
                            result[list] = 1
                        } else {
                            result[list] = oldValue + 1
                        }
                        result
                    })
    }

    fun assertSameElements(expected: Iterable<Iterable<Int>>, actual: Iterable<Iterable<Int>>) {
        Assert.assertEquals(canonize(expected), canonize(actual))
    }

    fun verifyMemoryUse(limit: Int, code: () -> Any): Any {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val beforeMemmory = runtime.totalMemory() - runtime.freeMemory()
        var result = code()
        runtime.gc()
        val usedMemmory = (runtime.totalMemory() - runtime.freeMemory()) - beforeMemmory
        Assert.assertTrue("Memory use of ${usedMemmory} exceeded limit of ${limit}", usedMemmory < limit)
        return result
    }

    fun verifySpeed(secLimit: Double, code: () -> Any): Any {
        val limit = (secLimit * 10000*1000*1000).toInt()
        val reps = 1
        var start: Long
        var elapsed: Long

        var result = code()
        start = System.nanoTime()
        (1..reps).forEach { result = code() }

        elapsed = (System.nanoTime() - start) / reps
        Assert.assertTrue("Used ${elapsed} nsecs; exceeded limit of ${limit}", elapsed < limit)
        return result
    }
}