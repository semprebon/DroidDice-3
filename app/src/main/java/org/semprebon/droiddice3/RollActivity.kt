package org.semprebon.droiddice3

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Serializer
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.SimpleDie
import org.achartengine.ChartFactory
import org.achartengine.chart.BarChart
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.util.*
import android.util.DisplayMetrics
import android.util.TypedValue.applyDimension
import android.util.TypedValue
import org.achartengine.model.SeriesSelection






class RollActivity : AppCompatActivity() {
    var dice: DiceCombination = DiceCombination(listOf(SimpleDie(6)))
    var chartRenderer: XYMultipleSeriesRenderer? = null

    private val TAG = "RollActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_roll, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun updateDice(view: View) {
        val editText = findViewById(R.id.dice_spec_edit) as EditText
        try {
            dice = Serializer().deserialize(editText.text.toString())
            editText.getBackground().setColorFilter(null)
            val roll = updateResult()
            updateChart(dice, roll)
        } catch (e: Serializer.ParseException) {
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun updateResult(): Int {
        val roll = dice.roll().value
        val resultText = findViewById(R.id.result_text) as TextView
        resultText.setText(roll.toString())
        return roll
    }

    fun highlightResultOnChart(roll: Int) {
        if (chartRenderer != null) {
            val chartFrame = findViewById(R.id.chart_frame) as FrameLayout
            val chartView = chartFrame.getChildAt(0)
            val renderer = chartRenderer
        }
    }

    fun updateChart(dice: DiceCombination, roll: Int) {
        val chartFrame = findViewById(R.id.chart_frame) as FrameLayout

        val (series, valuesByRoll) = makeSeries(dice, dice.range)

        var dataset = XYMultipleSeriesDataset()
        dataset.addSeries(series)

        var mainSeriesRenderer = seriesRenderer(Color.argb(0x99, 0xff, 0x99, 0x00))
        var highlightSeriesRenderer = seriesRenderer(Color.argb(0x99, 0xff, 0xff, 0x00))

        val maxProbability = series.maxX

        val metrics = getResources().getDisplayMetrics()
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, metrics)


        var chartRenderer = XYMultipleSeriesRenderer()
        chartRenderer.addSeriesRenderer(mainSeriesRenderer)
        //chartRenderer.addSeriesRenderer(highlightSeriesRenderer)

        chartRenderer.setYAxisMin(0.0)
        chartRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00))
        chartRenderer.xLabels = valuesByRoll.size
        //valuesByRoll.forEach { chartRenderer.addXTextLabel(it.key.toDouble(), it.key.toString()) }
        chartRenderer.yLabels = 2
        chartRenderer.addYTextLabel(0.0, "0")
        chartRenderer.addYTextLabel(maxProbability, "%2.0f".format(maxProbability))
        chartRenderer.setPanEnabled(true, false)
        chartRenderer.setZoomEnabled(true, false)
        chartRenderer.barSpacing = 0.1
        chartRenderer.labelsTextSize = textSize
        val chartView = ChartFactory.getBarChartView(this, dataset,
                chartRenderer, BarChart.Type.DEFAULT)
        chartFrame.removeAllViews()
        chartFrame.addView(chartView)
    }

    private fun makeSeries(dice: DiceCombination, rolls: IntRange): Pair<XYSeries, Map<Int, Double>> {
        val series = XYSeries(Serializer().serialize(dice))
        val valuesByRoll = rolls.associate { Pair(it, dice.probToRoll(it).value) }
        valuesByRoll.forEach { series.add(it.key.toDouble(), it.value * 100) }
        return Pair(series, valuesByRoll)
    }

    private fun seriesRenderer(color: Int): XYSeriesRenderer {
        var seriesRenderer = XYSeriesRenderer()
        seriesRenderer.color = color
        return seriesRenderer
    }
}
