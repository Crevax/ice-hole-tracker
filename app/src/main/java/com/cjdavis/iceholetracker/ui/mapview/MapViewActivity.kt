package com.cjdavis.iceholetracker.ui.mapview

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.cjdavis.iceholetracker.R
import com.cjdavis.iceholetracker.databinding.ActivityMapViewBinding
import com.cjdavis.iceholetracker.ui.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_map_view.*

class MapViewActivity : BaseActivity<MapViewViewModel, ActivityMapViewBinding>(),
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    override val viewModelClassToken = MapViewViewModel::class.java
    override val layoutId = R.layout.activity_map_view

    private val mGoogleApiClient by lazy {
        GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }
    private val mLocationRequest by lazy {
        LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong())
                .setFastestInterval((1 * 1000).toLong())
    }
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
        mGoogleApiClient.connect()
        btnSubmit.isEnabled = false

        vm.checkFile()
    }

    override fun onPause() {
        super.onPause()

        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient.disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }

        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.errorCode)
        }
    }

    override fun onLocationChanged(location: Location) {
        vm.currentLocation.set(location)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
        btnSubmit.isEnabled = location.accuracy <= MIN_ACCURACY
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
    }

    companion object {

        // TODO: Rewrite App so everything doesn't happen in one activity
        val TAG: String = MapViewActivity::class.java.simpleName

        /*
         * Define a request code to send to Google Play services
         * This code is returned in Activity.onActivityResult
         */
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        private const val MIN_ACCURACY = 20.0f
    }
}
