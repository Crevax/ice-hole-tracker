package com.cjdavis.iceholetracker

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
import kotlinx.android.synthetic.main.activity_home_screen.*
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import com.cjdavis.iceholetracker.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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

    private var binding: ActivityHomeScreenBinding? = null
    private lateinit var vm: HomeScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        vm = ViewModelProviders.of(this).get(HomeScreenViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen)
        binding?.setVariable(BR.vm, vm)
        subscribeUI()
        vm.start()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
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
        btnSubmit.isEnabled = location.accuracy <= MIN_ACCURACY
    }

    private fun subscribeUI() {
        vm.userMsg.observe(this, Observer { msg ->
            Toast.makeText(applicationContext, msg,
                    Toast.LENGTH_LONG).show()
        })
    }

    companion object {

        // TODO: Rewrite App so everything doesn't happen in one activity
        val TAG: String = HomeScreen::class.java.simpleName

        /*
         * Define a request code to send to Google Play services
         * This code is returned in Activity.onActivityResult
         */
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        private const val MIN_ACCURACY = 20.0f
    }
}
