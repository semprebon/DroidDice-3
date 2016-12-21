package org.semprebon.droiddice3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Probability
import java.util.*

/**
 * Created by Andrew on 12/17/2016.
 */
class ChartView(context: Context, attributes: AttributeSet) : View(context, attributes) {
    private val TAG = "ChartView"
    var barColor = Color.argb(0xff, 0xff, 0xec, 0xb3)
    var selectedBarColor = Color.argb(0xff, 0xff, 0xc1, 0xb3)
    var rolledBarColor = Color.argb(0xff, 0xff, 0x52, 0x52)
    var selectedRolledBarColor = Color.argb(0xff, 0xd3, 0x2f, 0x2f)

    data class Bar(val index: Int, val value: Double, val selected: Boolean, val rolled: Boolean)

    private var bars: List<Bar> = ArrayList<Bar>()
    var maxValue = 0.0
    var minIndex = 0
    var maxIndex = 0

    val BAR_GAP = 3

    fun getBars() = bars
    fun setBars(bars: List<Bar>) {
        this.bars = bars
        maxValue = bars.fold(0.0) { max, bar -> if (bar.value > max) bar.value else max }
        minIndex = bars.map { it.index }.reduce { a,b -> if (a < b) a else b }
        maxIndex = bars.map { it.index }.reduce { a,b -> if (a > b) a else b }
    }

    fun getBar(index: Int): Bar? {
        return bars.find { it.index == index }
    }

    fun setBar(index: Int, bar: Bar) {
        val i = bars.indexOfFirst { it.index == index }
        bars = bars.minus(bars[i]).plus(bar)
    }


    override fun onDraw(canvas: Canvas) {
        if (bars == null || bars.count() == 0) return
        canvas.drawColor(Color.LTGRAY)
        val paints =
                arrayOf(barColor, selectedBarColor, rolledBarColor, selectedRolledBarColor).
                        map { paintForColor(it) }
        bars.forEachIndexed { i, bar ->
            val x0 = indexToX(bar.index)
            val x1 = indexToX(bar.index + 1) - BAR_GAP
            val y0 = valueToY(0.0)
            val y1 = valueToY((bar.value))
            canvas.drawRect(x0, y0, x1, y1, paints[colorIndexForBar(bar)])
        }
    }

    private fun paintForColor(color: Int): Paint {
        val p = Paint()
        p.color = color
        return p
    }

    private fun colorIndexForBar(bar: Bar) =
            (if (bar.selected) 1 else 0) + (if (bar.rolled) 2 else 0)

    private fun indexToX(index: Int) = ((index - minIndex) * width).toFloat() / (maxIndex + 1 - minIndex)
    private fun valueToY(value: Double) = height - ((value * height) / maxValue).toFloat()

    public fun xToIndex(x: Float) = ((x*(maxIndex + 1 - minIndex)) / width - 0.5).toInt() + minIndex
    public fun yToValue(y: Float) = ((height - y) * maxValue / height).toDouble()
}