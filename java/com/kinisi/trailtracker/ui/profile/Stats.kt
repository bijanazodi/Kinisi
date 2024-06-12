package com.kinisi.trailtracker.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.kinisi.trailtracker.R
import kotlinx.android.synthetic.main.activity_stats.*
import android.R.attr.data
import android.R.attr.data
import android.R.attr.data
import android.R.attr.data
import android.content.ContentValues
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.osmdroid.util.Distance


class Stats: AppCompatActivity () {
    var Distance: java.util.ArrayList<Double> = java.util.ArrayList()
    var FloatDistance = 0f
    var FloatDistance2 = 0f
    var FloatDistance3 = 0f
    var totaldistance = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        readDb()
    }

    private fun setBarChart() {
        title = "Stats"
        val bChart = findViewById<BarChart>(R.id.progressBarChart)
        println(bChart)


        //x values
        val labels = ArrayList<String>()
        labels.add("Sunday")
        labels.add("Monday")
        labels.add("Tuesday")
        labels.add("Wednesday")
        labels.add("Thursday")
        labels.add("Friday")
        labels.add("Saturday")

        //y values
        val entries = ArrayList<BarEntry>()//: MutableList<BarEntry> = ArrayList()
        entries.add(BarEntry(1f, 0f))
        entries.add(BarEntry(2f, 0f))
        entries.add(BarEntry(3f, 0f))
        entries.add(BarEntry(4f, 0f))
        entries.add(BarEntry(5f, 0f))
        entries.add(BarEntry(6f, totaldistance))
        entries.add(BarEntry(7f, 0f))
        entries.add(BarEntry(8f, 0f))
        entries.add(BarEntry(9f, 0f))
        entries.add(BarEntry(10f, 0f))
        entries.add(BarEntry(11f, 0f))
        entries.add(BarEntry(12f, 0f))


        //bar data set
        val set = BarDataSet(entries, "BarDataSet")
        set.setColor(resources.getColor(R.color.purple_200))
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width

        bChart.data = data
        bChart.setFitBars(true) // make the x-axis fit exactly all bars

        bChart.invalidate() // refresh
        bChart.setGridBackgroundColor(resources.getColor(R.color.purple_200))

        bChart.animateY(5000)
        //        hide grid lines
        bChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = bChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        bChart.axisRight.isEnabled = false

        //remove legend
//        bChart.legend.isEnabled = false

        //remove description label
        //bChart.description.isEnabled = false
        bChart.description.text = "Miles Moved This Year"
        bChart.description.setPosition(700f,70f)
        bChart.description.setTextSize(20f)
        // bChart.description.setPosition()

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.valueFormatter = ""
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

    }

    private fun readDb()
    {
        val docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    Distance = document.get("userTotalDistance") as java.util.ArrayList<Double>
                    FloatDistance = Distance.sum().toFloat()
                    totaldistance = FloatDistance
                    setBarChart()
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

    }
}