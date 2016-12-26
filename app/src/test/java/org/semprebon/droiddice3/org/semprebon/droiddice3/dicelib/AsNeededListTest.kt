package org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib

import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by semprebon on 12/25/16.
 */
class AsNeededListTest {

    val baseList = listOf(1,2,3,6,10,15,25)

    @Test
    fun getsFirstElement() {
        val list = AsNeededList(baseList.iterator())
        assertEquals(1, list.get(0))
    }

    @Test
    fun getLaterElement() {
        val list = AsNeededList(baseList.iterator())
        assertEquals(10, list.get(4))
    }

    @Test
    fun indexOf() {
        val list = AsNeededList(baseList.iterator())
        assertEquals(2, list.indexOf(3))
    }
}