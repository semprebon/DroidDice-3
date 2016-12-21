package org.semprebon.droiddice3

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * Created by Andrew on 12/18/2016.
 */
class ChartOnTouchListener(val context: RollActivity, val chartView: ChartView) :
        GestureDetector.SimpleOnGestureListener() {
    private val TAG = "ChartOnTouchListener"

    override fun onDown(event: MotionEvent): Boolean {
        Log.v(TAG, "Tap down on ${event?.x},${event?.y}")
        return true
    }

    override fun onDoubleTap(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        Log.v(TAG, "Double Tap up on ${x},${y}")
        if (x != null && y != null) {
            val roll: Int = chartView.xToIndex(x)
            val bar = chartView.getBar(roll)
            if (bar != null) {
                val selected = bar.selected
                val nextSelected = chartView.getBar(roll+1)?.selected ?: false
                val prevSelected = chartView.getBar(roll-1)?.selected ?: false
                val range =
                        if (nextSelected == prevSelected) {
                            roll..roll
                        } else if (nextSelected) {
                            chartView.minIndex..roll
                        } else {
                            roll..chartView.maxIndex
                        }
                range.forEach { r ->
                    val newBar = chartView.getBar(r)?.copy(selected = !selected)
                    if (newBar != null) {
                        chartView.setBar(r, newBar)
                    }
                }
                chartView.invalidate()
                context.updateProbability()
                return true
            }
        }
        return super.onDoubleTap(event)
    }

     override fun onSingleTapUp(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        if (x != null && y != null) {
            val roll: Int = chartView.xToIndex(x)
            val bar = chartView.getBar(roll)
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
}