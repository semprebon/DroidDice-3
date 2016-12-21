package org.semprebon.droiddice3

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
import android.util.Log
import android.util.TypedValue.applyDimension
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import org.achartengine.GraphicalView
import org.achartengine.chart.AbstractChart
import org.achartengine.model.SeriesSelection
import android.view.MotionEvent
import android.text.method.Touch.onTouchEvent



/**
 * TODO: Select range on graph to display probabilty
 * TODO: Save dice
 * TODO: Handle rolls with many possibilities gracefully: 3d6!
 * TODO: Keyboard/buttons for entering dice
 * TODO: Color scheme!
 * TODO: Rolling wrong values (79, 102, etc) for 2d6!
 * TODO: Label bars?
 */
class RollActivity : AppCompatActivity() {

    var dice: DiceCombination = DiceCombination(listOf(SimpleDie(6)))
    var detector: GestureDetectorCompat? = null

    companion object {
        private val TAG = "RollActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val editText = findViewById(R.id.dice_spec_edit) as EditText
        editText.setText(Serializer().serialize(dice), TextView.BufferType.NORMAL)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val chartView = findViewById(R.id.chart_view) as ChartView
        detector = GestureDetectorCompat(this, ChartOnTouchListener(this, chartView))
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.detector?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun updateDice(view: View) {
        val editText = findViewById(R.id.dice_spec_edit) as EditText
        try {
            dice = Serializer().deserialize(editText.text.toString())
            editText.getBackground().setColorFilter(null)
            val roll = updateResult()
            val bars =
                    dice.valuesByRoll().
                    map { ChartView.Bar(it.key, it.value, false, roll == it.key)}
            val chartView = findViewById(R.id.chart_view) as ChartView
            chartView.setBars(bars)
            chartView.invalidate()
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

    fun updateProbability() {
        val resultText = findViewById(R.id.probability_text) as TextView
        val chartView = findViewById(R.id.chart_view) as ChartView
        val probability =
                chartView.getBars().filter { it.selected }.map { it.value }.reduce { a,b -> a+b }
        resultText.setText(probability.toString())
    }
}
