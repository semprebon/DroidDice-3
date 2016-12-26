package org.semprebon.droiddice3

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * Implements top to select/unselect, scrool to select multiple bars
 */
class ChartOnTouchListener(val context: RollActivity, val chartView: ChartView) :
        GestureDetector.SimpleOnGestureListener() {
    private val TAG = "ChartOnTouchListener"
    private var inScroll = false
    private var selectedTo = false

    override fun onDown(event: MotionEvent): Boolean {
        inScroll = false
        return true
    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        if (x != null && y != null) {
            val value: Int = chartView.xToIndex(x)
            val bar = chartView.getBar(value)
            if (bar != null) {
                val newBar = bar.copy(selected = !bar.selected)
                chartView.setBar(newBar.index, newBar)
                chartView.invalidate()
                context.updateProbability()
            }
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