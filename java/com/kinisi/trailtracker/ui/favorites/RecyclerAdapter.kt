package com.kinisi.trailtracker.ui.favorites

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kinisi.trailtracker.R
import org.osmdroid.util.Distance
import java.net.URL
import java.util.ArrayList

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var Distance: java.util.ArrayList<Double> = java.util.ArrayList()
    var Speed: java.util.ArrayList<Double> = java.util.ArrayList()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView = itemView.findViewById(R.id.title)
        var itemType: TextView = itemView.findViewById(R.id.type)
        var itemDesc: TextView = itemView.findViewById(R.id.description)

    }



    private val type1 = arrayOf("",
        "", "",
        "", "",
        "", "")

    private val description1 = arrayOf("",
        "", "",
        "", "",
        "", "")


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.frame_trailview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    Distance = document.get("userTotalDistance") as java.util.ArrayList<Double>
                    viewHolder.itemTitle.text = "Total Distance: " + Distance[Distance.size - i-1].toString() + " Miles"

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        val ref =  Firebase.firestore.collection("userAverageSpeed").document("ciJDSJnCFn6yCU9et7qK")
        ref.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    Speed = document.get("userAverageSpeed") as java.util.ArrayList<Double>
                    viewHolder.itemDesc.text = "Average Speed: " + Speed[Speed.size - i-1].toString() + " MPH"

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        viewHolder.itemType.text = type1[i]
        //viewHolder.itemDesc.text = description1[i]

    }

    override fun getItemCount(): Int {
        return 7
    }


}