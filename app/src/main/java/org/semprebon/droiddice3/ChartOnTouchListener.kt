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
        val selectedBar = barFromEvent(event)
        if (selectedBar != null) {
            val lowerSelected = chartView.getBar(selectedBar.index - 1)?.selected
            val higherSelected = chartView.getBar(selectedBar.index + 1)?.selected
            val selection =
                    if (!selectedBar.selected) JUST_THIS
                    else if (lowerSelected != true && higherSelected == false) ALL_ABOVE
                    else if (higherSelected != false && lowerSelected == false) ALL_BELOW
                    else NONE_SELECTED

            (chartView.minIndex..chartView.maxIndex).forEach {
                val bar = chartView.getBar(it)
                val desiredSelected = selection(selectedBar.index, it)
                if (bar != null && desiredSelected != bar.selected) {
                    val newBar = bar.copy(selected = desiredSelected)
                    chartView.setBar(newBar.index, newBar)
                }
            }
            chartView.invalidate()
            context.updateProbability()
            return true
        }
        return super.onSingleTapUp(event)
    }

    private fun barFromEvent(event: MotionEvent?) : ChartView.Bar? {
        val x = event?.x
        if (x != null) {
            val roll: Int = chartView.xToIndex(x)
            return chartView.getBar(roll)
        } else {
            return null
        }
    }

}