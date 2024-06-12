package com.kinisi.trailtracker.ui.home

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.kinisi.trailtracker.R
import com.kinisi.trailtracker.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.LineData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import com.google.firebase.firestore.QueryDocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.Source
import org.osmdroid.util.Distance
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    var Distance: java.util.ArrayList<Double> = java.util.ArrayList()
    var FloatDistance = 0f
    var FloatDistance2 = 0f
    var FloatDistance3 = 0f
    var count = 0
    var initial = 0f

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        setDblog()
        readDb()

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLineChart( bChart:BarChart, lChart:LineChart) {
        // var title = "Bar Chart"
        //x values
        var initialday = 0f
        val cal: Calendar = Calendar.getInstance()
        val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_WEEK)
        when (day) {
            Calendar.MONDAY -> {initialday = 1f}
            Calendar.TUESDAY -> {initialday = 2f}
            Calendar.WEDNESDAY -> {initialday = 3f}
            Calendar.THURSDAY -> {initialday = 4f}
            Calendar.FRIDAY -> {initialday = 5f}
            Calendar.SATURDAY -> {initialday = 6f}
            Calendar.SUNDAY -> {initialday = 7f}
        }

        val dayOfMonthStr = dayOfMonth.toFloat()
        var initial = dayOfMonthStr-6
        val docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    Distance = document.get("userTotalDistance") as java.util.ArrayList<Double>
                    FloatDistance = Distance[0].toFloat()
                    FloatDistance2 = Distance[1].toFloat()
                    FloatDistance3 = Distance[2].toFloat()

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        val lEntries = ArrayList<Entry>()
        for (i in 1..dayOfMonth) {
            FloatDistance = Distance[Distance.size-dayOfMonth+i-1].toFloat()
            lEntries.add(Entry(i.toFloat(),FloatDistance ))
            initial+=1f
        }
        if (dayOfMonth!=31){
            for (i in dayOfMonth+1..31) {
                lEntries.add(Entry(i.toFloat(),0f ))
            }
        }
        val entries = ArrayList<BarEntry>()
        for (x in 1..initialday.toInt()) {
            FloatDistance = Distance[Distance.size-initialday.toInt()+x-1].toFloat()
            entries.add(BarEntry(x.toFloat(), FloatDistance))
            initialday+=0f
        }
        if (day!=7){
            for (i in day+1..7) {
                entries.add(BarEntry(i.toFloat(), 0f))
            }
        }
        val labels = ArrayList<String>()
        labels.add("Sunday")
        labels.add("Monday")
        labels.add("Tuesday")
        labels.add("Wednesday")
        labels.add("Thursday")
        labels.add("Friday")
        labels.add("Saturday")

        //y values
        //val entries = ArrayList<BarEntry>()//: MutableList<BarEntry> = ArrayList()
        /*entries.add(BarEntry(1f, FloatDistance))
        entries.add(BarEntry(2f, FloatDistance2))
        entries.add(BarEntry(3f, FloatDistance3))
        entries.add(BarEntry(4f, 0f))
        entries.add(BarEntry(5f,0f))
        entries.add(BarEntry(6f, 0f))
        entries.add(BarEntry(7f, 0f))*/


        //bar data set
        val set = BarDataSet(entries, "BarDataSet")
        set.setColor(resources.getColor(R.color.purple_200))
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width
        bChart.data = data

        //        hide grid lines
        bChart.axisLeft.setDrawGridLines(false)
        val xAxis1: XAxis = bChart.xAxis
        xAxis1.setDrawGridLines(false)
//        xAxis1.setDrawAxisLine(false)

        //remove right y-axis
        bChart.axisRight.isEnabled = false

        bChart.description.text = "Miles Moved This Week"
        bChart.description.setPosition(700f, 70f)
        bChart.description.setTextSize(15f)

        // to draw label on xAxis
        xAxis1.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.valueFormatter = ""
        xAxis1.setDrawLabels(true)
        xAxis1.granularity = 1f
        xAxis1.labelRotationAngle = +90f

        bChart.setFitBars(true) // make the x-axis fit exactly all bars

//                remove legend
//        bChart.legend.isEnabled = false


        bChart.invalidate() // refresh
        bChart.setGridBackgroundColor(resources.getColor(R.color.purple_200))

        bChart.animateY(5000)



//         ******************************************************************
//        LineChart

        //x values
        val labels2 = ArrayList<String>()
        labels2.add("Sunday")
        labels2.add("Monday")
        labels2.add("Tuesday")
        labels2.add("Wednesday")
        labels2.add("Thursday")
        labels2.add("Friday")
        labels2.add("Saturday")




        //y values
        /*val lEntries = ArrayList<Entry>()
        lEntries.add(Entry(0f,0f ))
        lEntries.add(Entry(1f, 0f))
        lEntries.add(Entry(2f, 0f))
        lEntries.add(Entry(3f, 0f))
        lEntries.add(Entry(4f, 0f))
        lEntries.add(Entry(5f, 0f))
        lEntries.add(Entry(6f, 0f))
        lEntries.add(Entry(7f, 0f))
        lEntries.add(Entry(8f, 0f))
        lEntries.add(Entry(9f, 0f))
        lEntries.add(Entry(10f, 0f))
        lEntries.add(Entry(11f, 0f))
        lEntries.add(Entry(12f, 0f))
        lEntries.add(Entry(13f, 0f))
        lEntries.add(Entry(14f, FloatDistance))
        lEntries.add(Entry(15f, FloatDistance2))
        lEntries.add(Entry(16f, FloatDistance3))
        lEntries.add(Entry(17f, 0f))
        lEntries.add(Entry(18f, 0f))
        lEntries.add(Entry(19f, 0f))
        lEntries.add(Entry(20f, 0f))
        lEntries.add(Entry(21f, 0f))
        lEntries.add(Entry(22f, 0f))
        lEntries.add(Entry(23f, 0f))
        lEntries.add(Entry(24f, 0f))
        lEntries.add(Entry(25f, 0f))
        lEntries.add(Entry(26f, 0f))
        lEntries.add(Entry(27f, 0f))
        lEntries.add(Entry(28f, 0f))
        lEntries.add(Entry(29f, 0f))
        lEntries.add(Entry(30f, 0f))*/



        //line data set
        val lSet = LineDataSet(lEntries, "LineDataSet")
        lSet.setColor(resources.getColor(R.color.teal_200))
        //val data = LineData(set)
        val lData = LineData(lSet)

        // set data
        if (lChart != null) {
            lChart.setData(lData)
            lChart.invalidate() // refresh
            lChart.setGridBackgroundColor(resources.getColor(R.color.purple_200))
            lChart.animateX(5000)


            if (lChart != null) {
                //hide grid lines
                lChart.axisLeft.setDrawGridLines(false)
                val xAxis: XAxis = lChart.xAxis
                xAxis.setDrawGridLines(false)
                // xAxis.setDrawAxisLine(false)

                // to draw label on xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                //xAxis.valueFormatter = ""
                xAxis.setDrawLabels(true)
                xAxis.granularity = 1f
                xAxis.labelRotationAngle = +90f
            }

            //remove right y-axis
            if (lChart != null) {
                lChart.axisRight.isEnabled = false
            }

            //remove legend

            if (lChart != null) {
                lChart.animateX(1000, Easing.EaseInSine)
            }

//        lChart.legend.isEnabled = false

            //remove description label
            //lChart.description.isEnabled = false
            lChart.description.text = "Miles Moved This Month"
            lChart.description.setPosition(700f, 70f)
            lChart.description.textSize = 15f

        }

    }

    private fun readDb()
    {
        val docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    Distance = document.get("userTotalDistance") as java.util.ArrayList<Double>
                    /*FloatDistance = Distance[count].toFloat()
                    FloatDistance2 = Distance[count+1].toFloat()
                    FloatDistance3 = Distance[count+2].toFloat()*/
                    val lChart: LineChart = binding.progressLineChart
                    val bChart: BarChart = binding.progressBarChart
                    setLineChart(bChart,lChart)


                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun setDblog(){
        var log = (java.sql.Timestamp(System.currentTimeMillis()))
        Firebase.firestore
            .collection("userAverageSpeed")
            .document("ciJDSJnCFn6yCU9et7qK")

        val time = HashMap<String, Any>()
        time["log"] = log
        Firebase.firestore.collection("userAverageSpeed").document("ciJDSJnCFn6yCU9et7qK")
            .update("log",time)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }

        Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
            .update("log",time)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }
        Firebase.firestore.collection("userActiveLocation").document("ciJDSJnCFn6yCU9et7qK")
            .update("log",time)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }

    }

}