package org.semprebon.droiddice3

import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.DiceCombination
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Serializer
import android.view.*
import android.view.MotionEvent
import android.widget.*
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.Probability
import org.semprebon.droiddice3.org.semprebon.droiddice3.dicelib.RollResult
import java.io.*


/**
 * Main activity of app - lets user roll dice
 *
 * FIXME: Update probability of hitting selection when dice are updated (or clear)
 * FIXME: Selection of bars is a little off
 * FIXME: Clear keyboard when exiting dice edit
 * TODO: Save selection with dice
 * TODO: On edit, default to current dice
 * TODO: Percent and result roll should be same color(s) as respective bars
 * TODO: Implement level of success mechanics
 * TODO: zoom and pan
 * TODO: Keyboard/buttons for entering dice
 */
class RollActivity : AppCompatActivity() {

    val serializer = Serializer()
    val defaultDice = serializer.deserialize("d6")
    val defaultDiceSets = mapOf(Pair("d6", defaultDice))
    val persister = DicePersister()

    var diceSets : MutableMap<String, DiceCombination> = copyAsMutable(defaultDiceSets)
    var dice: DiceCombination = defaultDice
    var detector: GestureDetectorCompat? = null
    var displayedRange: IntRange = dice.range(0.01)
    var adapter : ArrayAdapter<String>? = null

    companion object { private val TAG = "RollActivity" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roll_activity)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val spinner = findViewById(R.id.dice_spec) as Spinner
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)

        fetchDice()
        updateDiceSets()

        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = dicePickListener

        val chartView = findViewById(R.id.chart_view) as ChartView
        detector = GestureDetectorCompat(this, ChartOnTouchListener(this, chartView))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.detector?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun updateDice(dice: DiceCombination) {
        this.dice = dice
        displayedRange = dice.range(0.01)
        updateResult()
    }

    fun updateChart(roll: RollResult) {
        val probabilitiesByValue =
                dice.probabilitiesByValue(displayedRange,
                        endCondition = dice.totalProbabilityOf(0.95))
        val bars = probabilitiesByValue.map { ChartView.Bar(it.key, it.value) }
        val chartView = findViewById(R.id.chart_view) as ChartView
        chartView.setBars(bars)
        chartView.rollIndex = roll.value
        chartView.invalidate()
    }

    /**
     * Triggered when user clicks button to roll the dice
     */
    fun roll(view: View) {
        updateResult()
    }

    /**
     * Triggered when user clicks button to edit dice
     */
    fun editDiceSpec(view: View) {
        val editView = findViewById(R.id.edit_dice)
        editView.visibility = View.VISIBLE
        findViewById(R.id.rollView).invalidate()
    }

    /**
     * Triggered when user clicks to save edited dice
     */
    fun saveDiceSpec(view: View) {
        val name = (findViewById(R.id.name_edit) as EditText).text.toString()
        val diceSpec = (findViewById(R.id.dice_spec_edit) as EditText).text.toString()

        diceSets.put(name, serializer.deserialize(diceSpec))
        updateDiceSets()
        closeDiceSpecEdit(view)
    }

    /**
     * Triggered when user clicks to cancel deiting dice
     */
    fun closeDiceSpecEdit(view: View) {
        findViewById(R.id.edit_dice).visibility = View.GONE
        findViewById(R.id.rollView).invalidate()
        findViewById(R.id.dice_spec).requestFocus()
    }

    /**
     * Called from ChartOnTouchListener to update probability selection
     */
    fun updateProbability() {
        val resultText = findViewById(R.id.probability_text) as TextView
        val chartView = findViewById(R.id.chart_view) as ChartView
        val limit = chartView.probabilityIndex
        val probability = when (chartView.probabilityType) {
            ChartView.ProbabilityType.NONE -> Probability.NEVER
            ChartView.ProbabilityType.EXACT -> dice.probToRoll(limit)
            ChartView.ProbabilityType.HIGH -> dice.probToBeat(limit)
            ChartView.ProbabilityType.LOW -> dice.probToRollOver(limit).not()
        }
        val rollSelected = chartView.rollIndex == chartView.probabilityIndex
        resultText.text = "${String.format("%.2f", probability.value*100.0)}%"
        resultText.setTextColor(
                if (rollSelected) chartView.selectedRolledBarColor
                else chartView.selectedBarColor)
    }

    /**
     * Updates the dice set selection widget and things dependent on what dice are available
     */
    fun updateDiceSets() {
        storeDice()
        adapter!!.clear()
        adapter!!.addAll(*diceSets.keys.toTypedArray())
        (findViewById(R.id.dice_spec) as Spinner).invalidate()
    }

    /**
     * Listener for handling interactions with the dice set selection widget
     */
    val dicePickListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val diceSpec = diceSets[parent?.getItemAtPosition(position)]
            if (diceSpec != null) {
                updateDice(diceSpec)
            }
        }
    }

    /**
     * Updates the chart and result text and any related views that are dependent
     * on the dice roll
     */
    private fun updateResult() {
        val roll = dice.roll()
        updateChart(roll)
        updateResultView()
    }

    fun updateResultView() {
        val resultText = findViewById(R.id.result_text) as TextView
        val chartView = findViewById(R.id.chart_view) as ChartView
        val roll = chartView.rollIndex

        if (roll != null) {
            resultText.text = chartView.rollIndex.toString()
            resultText.setTextColor(
                    if (chartView.isSelected(roll)) chartView.selectedRolledBarColor
                    else chartView.rolledBarColor)
        }
    }

    private fun <K, V>copyAsMutable(map: Map<K, V>) = mutableMapOf(*map.map { it.toPair() }.toTypedArray())

    /**
     * Update the dice set selection from storage
     */
    private fun fetchDice() {
        diceSets = persister.loadDice(File(filesDir, "dice")) ?: copyAsMutable(defaultDiceSets)
    }

    /**
     * Update storage with the current dice set selection
     */
    private fun storeDice() {
        persister.saveDice(FileOutputStream(File(filesDir, "dice")), diceSets)
    }

}
