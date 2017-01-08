package org.semprebon.droiddice3

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Implements top to select/unselect, scroll to select multiple bars
 */
class ChartOnTouchListener(val context: RollActivity, val chartView: ChartView) :
        GestureDetector.SimpleOnGestureListener() {

    companion object {
        private val TAG = "ChartOnTouchListener"

        private val JUST_THIS: (Int, Int) -> Boolean = { selectedIndex, index -> index == selectedIndex }
        private val ALL_ABOVE: (Int, Int) -> Boolean = { selectedIndex, index -> index >= selectedIndex }
        private val ALL_BELOW: (Int, Int) -> Boolean = { selectedIndex, index -> index <= selectedIndex }
        private val NONE_SELECTED: (Int, Int) -> Boolean = { selectedIndex, index -> false }
    }

    private var inScroll = false

    override fun onDown(event: MotionEvent): Boolean {
        inScroll = false
        return true
    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        val index = indexFromEvent(event)
        if (index != null) {
            chartView.incrementProbabilityType()
            chartView.probabilityIndex = index
            chartView.invalidate()
            context.updateProbability()
            context.updateResultView()
            return true
        }
        return super.onSingleTapUp(event)
    }

    private fun indexFromEvent(event: MotionEvent?) : Int? {
        val x = event?.x
        if (x != null) {
            return chartView.xToIndex(x)
        } else {
            return null
        }
    }

}