package org.semprebon.droiddice3

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Serializer
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.SimpleDie
import org.achartengine.GraphicalView
import org.achartengine.ChartFactory.getBarChartView
import org.achartengine.ChartFactory
import org.achartengine.chart.BarChart
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.SimpleSeriesRenderer
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer


class RollActivity : AppCompatActivity() {
    var dice: DiceCombination = DiceCombination(listOf(SimpleDie(6)))

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
            updateResult()
            updateChart(dice)
        } catch (e: Serializer.ParseException) {
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun updateResult() {
        val roll = dice.roll().value
        val resultText = findViewById(R.id.result_text) as TextView
        resultText.setText(roll.toString())
    }

    fun updateChart(dice: DiceCombination) {
        val chartFrame = findViewById(R.id.chart_frame) as FrameLayout
        var series = XYSeries(Serializer().serialize(dice))
        dice.range.forEach { series.add(it.toDouble(), dice.probToRoll(it).value*100) }
        var dataset = XYMultipleSeriesDataset()
        dataset.addSeries(series)

        var probabilitySeriesRenderer = XYSeriesRenderer()

        var probabilityHistogramDataRenderer = XYMultipleSeriesRenderer()
        probabilityHistogramDataRenderer.addSeriesRenderer(probabilitySeriesRenderer)
        probabilityHistogramDataRenderer.setYAxisMin(0.0)

        val chartView = ChartFactory.getBarChartView(this, dataset,
                probabilityHistogramDataRenderer, BarChart.Type.DEFAULT)
        chartFrame.removeAllViews()
        chartFrame.addView(chartView)

    }
}
