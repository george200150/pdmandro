package com.george200150.uni.pdmandro.todo.maps

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.george200150.uni.pdmandro.R
import com.george200150.uni.pdmandro.todo.data.local.LocationHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class EventsActivity : AppCompatActivity(), OnMapClickListener,
    OnMapLongClickListener, OnCameraIdleListener, OnMapReadyCallback {

    private lateinit var tapTextView: TextView
    private lateinit var cameraTextView: TextView
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        tapTextView = findViewById(R.id.tap_text)
        cameraTextView = findViewById(R.id.camera_text)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // return early if the map was not initialised properly
        map = googleMap ?: return
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnCameraIdleListener(this)
    }

    override fun onMapClick(point: LatLng) {
        tapTextView.text = "tapped, point=$point"
        LocationHelper.setLocation(point.latitude.toFloat(), point.longitude.toFloat())
    }

    override fun onMapLongClick(point: LatLng) {
        tapTextView.text = "long pressed, point=$point"
    }

    override fun onCameraIdle() {
        if (!::map.isInitialized) return
        cameraTextView.text = map.cameraPosition.toString()
    }
}