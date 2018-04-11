package com.cjdavis.iceholetracker.ui.mapview

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.cjdavis.iceholetracker.R
import com.cjdavis.iceholetracker.databinding.ActivityMapViewBinding
import com.cjdavis.iceholetracker.ui.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks.call
import kotlinx.android.synthetic.main.activity_map_view.*

class MapViewActivity : BaseActivity<MapViewViewModel, ActivityMapViewBinding>(),
        OnMapReadyCallback {

    override val viewModelClassToken = MapViewViewModel::class.java
    override val layoutId = R.layout.activity_map_view


    private val mapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    }
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapFragment?.getMapAsync(this)
    }


    override fun onResume() {
        super.onResume()
        btnSubmit.isEnabled = false

        vm.checkFile()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun subscribeUI() {
        vm.userMsg.observe(this, Observer { msg ->
            Toast.makeText(applicationContext, msg,
                    Toast.LENGTH_LONG).show()
        })

        vm.currentLocation.observe(this, Observer { location->
            location?.let {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                btnSubmit.isEnabled = location.accuracy <= MIN_ACCURACY
            } ?: call { btnSubmit.isEnabled = false }
        })
    }

    companion object {

        // TODO: Rewrite App so everything doesn't happen in one activity
        val TAG: String = MapViewActivity::class.java.simpleName

        private const val MIN_ACCURACY = 20.0f
    }
}
