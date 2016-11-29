package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import org.junit.Assert
import java.util.*

/**
 * Created by Andrew on 11/26/2016.
 */
interface TestSupport {
    fun canonize(list: List<List<Int>>): Map<List<Int>, Int> {
        return list.fold(HashMap<List<Int>, Int>(),
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

    fun assertSameElements(expected: List<List<Int>>, actual: List<List<Int>>) {
        Assert.assertEquals(canonize(expected), canonize(actual))
    }

}