package com.kinisi.trailtracker.ui.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import com.kinisi.trailtracker.R
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.*
import android.R.attr.name
import android.R.attr.name
import android.content.ContentValues
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.google.firebase.firestore.FieldValue
import org.osmdroid.util.Distance
import android.provider.Settings.Secure
import java.security.AccessController.getContext


class Speedometer: AppCompatActivity(), OnMapReadyCallback {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    var init_Lat = 0.0
    var init_long = 0.0
    var deltaLngMeters = 0.0
    var deltaLatMeters = 0.0
    var marker = LatLng(0.0,0.0)
    var i = 0
    var x = 0
    var distance = 0.0
    var speed = 0.0f
    lateinit var btnStartupdate: Button
    lateinit var btnStopUpdates: Button
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView
    lateinit var txtCalories: TextView
    lateinit var txtDistance: TextView
    lateinit var txtSpeed: TextView
    var previousLocation: Location? = null
    private lateinit var mMap: GoogleMap
    var current = 0
    var locations: ArrayList<LatLng> = ArrayList()
    var Distance: ArrayList<Double> = ArrayList()
    var Speed: ArrayList<Float> = ArrayList()
    var averageSpeed: ArrayList<Float> = ArrayList()
    var Distance_arr: ArrayList<Double> = ArrayList()
    var count = 0
    private var polyline: Polyline? = null
    /*val id: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)*/

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(com.kinisi.trailtracker.R.layout.activity_speedometer)
        mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }
        btnStartupdate = findViewById(R.id.btn_start_upds)
        btnStopUpdates = findViewById(R.id.btn_stop_upds)
        txtLat = findViewById(R.id.txtLat)
        txtLong = findViewById(R.id.txtLong)
        //txtTime = findViewById(R.id.txtTime)
        txtDistance = findViewById(R.id.txtDistance)
        txtSpeed = findViewById(R.id.txtSpeed)
        txtCalories = findViewById(R.id.txtCalories)


        var startTime = LocalTime.MAX
        var stopTime = LocalTime.MAX
        var durationTime = LocalTime.MIN




        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }


        btnStartupdate.setOnClickListener {
            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
                btnStartupdate.isEnabled = false
                btnStopUpdates.isEnabled = true
                val simpleChronometer = findViewById(R.id.simpleChronometer) as Chronometer
                simpleChronometer.setBase(SystemClock.elapsedRealtime())
                simpleChronometer.start()
                simpleChronometer.setFormat("Timer:   %s")
                simpleChronometer.start()
                startTime=LocalTime.now()

            }
        }

        btnStopUpdates.setOnClickListener {
            stoplocationUpdates()
            // txtTime.text = "Updates Stopped"
            btnStartupdate.isEnabled = true
            btnStopUpdates.isEnabled = false
            val simpleChronometer = findViewById(R.id.simpleChronometer) as Chronometer
            simpleChronometer.stop()
            stopTime=LocalTime.now()

            durationTime= stopTime.minusHours(startTime.hour.toLong()).minusMinutes(startTime.minute.toLong()).minusSeconds(startTime.second.toLong())
            txtCalories.text = ("Calories Burned: "+ calcCals(durationTime).toString())

        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
                true
            }else{
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val mLocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onLocationChanged(location: Location) {
        // New location has now been determined\
        if (x ==0)
        {
            init_Lat = location.latitude
            init_long = location.longitude
            x++
        }
        deltaLatMeters = location.latitude - init_Lat
        deltaLngMeters = location.longitude - init_long
        val date: Date = (Calendar.getInstance().time)
        val sdf = SimpleDateFormat("hh:mm:ss a")
        //txtTime.text = "Updated at : " + sdf.format(date)
        txtLat.text = "LATITUDE : " + location.latitude
        txtLong.text = "LONGITUDE : " + location.longitude
        distance = truncate((sqrt((deltaLngMeters*deltaLngMeters) + (deltaLatMeters*deltaLatMeters))*11000.57))
        txtDistance.text = "Distance " + distance/100 + "Miles"
        speed = truncate((location.getSpeed()) *360)
        txtSpeed.text = "Speed " + speed/100 +"MPH"
        if (i ==0)
        {
            marker = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(marker))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15F))
            i++
        }
        var latLng = LatLng(location.latitude, location.longitude)
        locations.add(latLng)
        mMap.addPolyline(PolylineOptions().color(Color.RED).addAll(locations))
        setDb()
        Speed.add(speed/100)






        count++



    }
    protected fun startLocationUpdates() {
        // Create the location request to start receiving updates
        readDb()
        mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        Looper.myLooper()?.let {
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
                it
            )
        }
    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        Distance.add(distance/100)
        Distance_arr.add(distance/100)
        setDbDistance()
        setDbSpeed()

    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }
    private fun setDb() {
        Firebase.firestore
            .collection("userActiveLocation")
            .document("ciJDSJnCFn6yCU9et7qK")

        val userdetail = HashMap<String, Any>()
        userdetail["userActiveLocation"] = locations
        Firebase.firestore.collection("userActiveLocation").document("ciJDSJnCFn6yCU9et7qK")
            .set(userdetail)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }


    }

    private fun setDbDistance(){

        Firebase.firestore
            .collection("userTotalDistance")
            .document("ciJDSJnCFn6yCU9et7qK")

        val userDistance = HashMap<String, Any>()
        userDistance["userTotalDistance"] = Distance
        Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
            .update(userDistance)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }


    }

    private fun setDbSpeed(){
        averageSpeed.add(Speed.average().toFloat())
        Firebase.firestore
            .collection("userAverageSpeed")
            .document("ciJDSJnCFn6yCU9et7qK")

        val userSpeed = HashMap<String, Any>()
        userSpeed["userAverageSpeed"] = averageSpeed
        Firebase.firestore.collection("userAverageSpeed").document("ciJDSJnCFn6yCU9et7qK")
            .update(userSpeed)
            .addOnSuccessListener { success ->

            }
            .addOnFailureListener { exception ->
                Log.e("Data Failed", "To added because ${exception}")
            }


    }
    private fun readDb()
    {
        var docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    Distance= document.get("userTotalDistance") as java.util.ArrayList<Double>


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        docRef =  Firebase.firestore.collection("userAverageSpeed").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    averageSpeed= document.get("userAverageSpeed") as java.util.ArrayList<Float>


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcCals(durationTime: LocalTime):Int{
        //CALORIE CALCULATOR

        val db=Firebase.firestore
        lateinit var auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val userId = user?.uid

        var speed: Double
        var duration : Double
        var TotalCaloriesBurned=0

        speed = Speed.average().toDouble()
        duration = 40.0 //get activity duration from Firebase
        Log.d("SPEED",speed.toString())
        duration = timeToMinutes(durationTime)
        Log.d("DURATION: ", duration.toString())





        val userInfoDocRef = Firebase.firestore
            .collection(("users").toString()).document("1qCgazCHTPQtnNSA9b3hdWrkmUs2")
        userInfoDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot user info data: ${document.data}")

                    val userinfo = document.get(userId.toString())
                    val weightKG =
                        (document.get("weight").toString().toInt().times(0.45359237)
                            .times(100)).roundToInt() / 100.0
                    Log.d("WEIGHT", weightKG.toString())

                    // METS X 3.5 X BW (KG) / 200 = KCAL/MIN.
                    val METS = mapOf<Double, Double>( //SPEED(mph) TO MET VALUE
                        0.0 to 0.0,
                        2.0 to 2.0,
                        3.0 to 3.0,
                        3.5 to 4.5,
                        4.0 to 6.0,
                        5.0 to 8.3,
                        5.2 to 9.0,
                        6.0 to 9.8,
                        6.7 to 10.5,
                        7.0 to 11.0,
                        7.5 to 11.5,
                        8.0 to 11.8,
                        8.6 to 12.3,
                        9.0 to 12.8,
                        10.0 to 14.5,
                        11.0 to 16.0,
                        12.0 to 19.0,
                        13.0 to 19.8,
                        14.0 to 23.0
                    )

                    var MET = 0.0

                    if (METS.containsKey(speed)) {
                        MET = METS.get(speed)!!
                    } else {
                        //Find closest MET for user's speed
                        var min = Double.MAX_VALUE
                        for (entry in METS.entries.iterator()) {
                            val diff = Math.abs(entry.key - speed)
                            if (diff < min) {
                                min = diff;
                                MET = entry.value
                            }

                        }
                    }
                    Log.d("MET: ", MET.toString())

                    TotalCaloriesBurned =
                        (MET * 3.5 * weightKG / 200 * duration).toInt()
                    Log.d("Calories Burned: ", TotalCaloriesBurned.toString())


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        return TotalCaloriesBurned;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timeToMinutes(t: LocalTime):Double{
        var duration :Double
        duration=(t.hour*60).toDouble()+ (t.minute).toDouble()+(t.second/60).toDouble()
        return duration
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }

}