package org.semprebon.droiddice3

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Serializer
import android.view.*
import android.view.MotionEvent
import android.widget.*
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.RollResult


/**
 * TODO: Selection should just be single bar, >- sleected bar, <= selected bar
 * TODO: Save dice
 * TODO: zoom and pan
 * TODO: Keyboard/buttons for entering dice
 * TODO: Color scheme!
 */
class RollActivity : AppCompatActivity() {

    val serializer = Serializer()
    val diceSets = mapOf(
            attrDice("Attr.Strength",       "d8!+d6![k1]"),
            attrDice("Attr.Agility",        "d10!+d6![k1]"),
            attrDice("Attr.Vigor",          "d8!+d6![k1]"),
            attrDice("Attr.Smarts",         "d4!+d6![k1]"),
            attrDice("Attr.Spirit",         "d6!+d6![k1]"),
            attrDice("Skill.Fighting",      "d10!+d6![k1]"),
            attrDice("Skill.Shooting",      "d8!+d6![k1]"),
            attrDice("Skill.Throwing",      "d6!+d6![k1]"),
            attrDice("Skill.Intimidation",  "d6!+d6![k1]"),
            attrDice("Damage.Sword",        "d8!+d8!"),
            attrDice("Damage.LongBow",      "d6!+d6!"),
            attrDice("Damage.Massive",      "d6!+d6!+d6!+d6!")
        )

    var dice: DiceCombination = diceSets.get("Attr.Strength") ?: serializer.deserialize("d6")
    var detector: GestureDetectorCompat? = null
    var displayedRange: IntRange = dice?.range(0.01)

    companion object { private val TAG = "RollActivity" }

    private fun attrDice(name: String, dice: String): Pair<String, DiceCombination> {
        return Pair(name, serializer.deserialize(dice))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val spinner = findViewById(R.id.dice_spec) as Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diceSets.keys.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener = dicePickListener

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

    fun updateDice(dice: DiceCombination) {
        this.dice = dice
        updateResult()
    }

    fun updateChart(roll: RollResult) {
        val probabilitiesByValue =
                dice.probabilitiesByValue(displayedRange,
                        endCondition = dice.totalProbabilityOf(0.95))
        val bars = probabilitiesByValue.
                        map { ChartView.Bar(it.key, it.value, false, roll.value == it.key)}
        val chartView = findViewById(R.id.chart_view) as ChartView
        chartView.setBars(bars)
        chartView.invalidate()
    }

    fun roll(view: View) {
        updateResult()
    }

    fun updateResult() {
        val roll = dice.roll()
        val resultText = findViewById(R.id.result_text) as TextView
        resultText.setText(roll.value.toString())
        updateChart(roll)
    }

    fun updateProbability() {
        val resultText = findViewById(R.id.probability_text) as TextView
        val chartView = findViewById(R.id.chart_view) as ChartView
        val probability =
                chartView.getBars().filter { it.selected }.map { it.value }.fold(0.0, { a,b -> a+b })
        resultText.setText("${String.format("%.2f", probability*100.0)}%")
    }

    val dicePickListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val diceSpec = diceSets.get(parent?.getItemAtPosition(position))
            if (diceSpec != null) {
                updateDice(diceSpec)
            }
        }
    }
}
