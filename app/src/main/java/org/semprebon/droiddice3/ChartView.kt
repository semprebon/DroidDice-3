package org.semprebon.droiddice3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.semprebon.droiddice3.R.attr.theme
import java.util.*
import android.util.TypedValue





/**
 * Displays bar chart of probabilities.
 */
class ChartView(context: Context, attributes: AttributeSet) : View(context, attributes) {
    private val TAG = "ChartView"

    val barColor = ResourcesCompat.getColor(resources, R.color.barColor, null)
    val selectedBarColor = ResourcesCompat.getColor(resources, R.color.selectedBarColor, null)
    val rolledBarColor = ResourcesCompat.getColor(resources, R.color.rolledBarColor, null)
    val selectedRolledBarColor = ResourcesCompat.getColor(resources, R.color.selectedRolledBarColor, null)

    var outlineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.0f, resources.displayMetrics)

    var probabilityIndex: Int = Int.MIN_VALUE
    var rollIndex: Int? = null

    enum class ProbabilityType { NONE, EXACT, HIGH, LOW }
    var probabilityType = ProbabilityType.EXACT

    fun incrementProbabilityType() {
        probabilityType = when (probabilityType) {
            ProbabilityType.NONE -> ProbabilityType.EXACT
            ProbabilityType.EXACT -> ProbabilityType.HIGH
            ProbabilityType.HIGH -> ProbabilityType.LOW
            ProbabilityType.LOW -> ProbabilityType.NONE
        }
    }

    data class Bar(val index: Int, val value: Double)

    fun isSelected(index: Int): Boolean {
        val limit = probabilityIndex
        return when (probabilityType) {
            ProbabilityType.NONE -> false
            ProbabilityType.EXACT -> index == probabilityIndex
            ProbabilityType.HIGH -> if (limit != null) index >= limit else false
            ProbabilityType.LOW -> if (limit != null) index <= limit else false
        }
    }

    fun isRolled(index: Int): Boolean {
        return index == rollIndex
    }

    private var bars: List<Bar> = ArrayList()
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
        if (bars.count() == 0) return
        val blackPaint = paintForColor(Color.BLACK)
        val barWidth = indexToX(minIndex+1) - indexToX(minIndex)
        blackPaint.textSize = barWidth*0.75f
        blackPaint.textAlign = Paint.Align.CENTER

        val fillPaints: Map<Pair<Boolean, Boolean>, Paint> =
                mapOf(Pair(Pair(false, false), paintForColor(barColor)),
                      Pair(Pair(false, true ), paintForColor(rolledBarColor)),
                      Pair(Pair(true,  false), paintForColor(selectedBarColor)),
                      Pair(Pair(true,  true ),  paintForColor(selectedRolledBarColor)))

        bars.forEachIndexed { i, bar ->
            val x0 = indexToX(bar.index)
            val x1 = indexToX(bar.index + 1) - BAR_GAP
            val y0 = valueToY(0.0)
            val y1 = valueToY((bar.value))

            canvas.drawRect(x0, y0, x1, y1, fillPaints[Pair(isSelected(bar.index), isRolled(bar.index))])
            canvas.drawText(bar.index.toString(), (x0 + x1) / 2, y0 - 5, blackPaint)
        }
    }

    private fun paintForColor(color: Int, style: Paint.Style = Paint.Style.FILL): Paint {
        val p = Paint()
        p.color = color
        p.style = style
        p.strokeWidth = outlineWidth
        return p
    }

    private fun indexToX(index: Int) = ((index - minIndex) * width).toFloat() / (maxIndex + 1 - minIndex)
    private fun valueToY(value: Double) = height - ((value * height) / maxValue).toFloat()

    fun xToIndex(x: Float) = ((x*(maxIndex + 1 - minIndex)) / width - 0.5).toInt() + minIndex
    fun yToValue(y: Float) = ((height - y) * maxValue / height)
}