package com.kinisi.trailtracker.ui.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.R
import android.content.Intent
import android.location.Address
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Transformations.map
import com.kinisi.trailtracker.MainActivity
import java.io.IOException
import java.util.*
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.kinisi.trailtracker.databinding.FragmentProfileBinding
import com.kinisi.trailtracker.ui.profile.ProfileViewModel
import com.kinisi.trailtracker.ui.profile.UpdateProfile
/*import com.google.android.gms.maps.MapView*/
import com.kinisi.trailtracker.databinding.FragmentSearchBinding
import com.mapbox.mapboxsdk.maps.MapboxMap;


class SearchFragment : Fragment() {
    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!


    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(47.0, -122.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))



    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfile
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //Brings to UpdateProfile on settings button click
        val speedbttn: Button = binding.speedbttn

        speedbttn.setOnClickListener {
            val intent = Intent(context, Speedometer::class.java)
            startActivity(intent)
        }


        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(com.kinisi.trailtracker.R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        val mapFragment = childFragmentManager.findFragmentById(com.kinisi.trailtracker.R.id.map) as SupportMapFragment?

        if (mapFragment != null) childFragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss()
    }
}
