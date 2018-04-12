package com.cjdavis.iceholetracker.service

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class CurrentLocationListener(private val googleApiClient: GoogleApiClient) :
        LiveData<Location>(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val locationRequest by lazy {
        LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong())
                .setFastestInterval((1 * 1000).toLong())
    }

    override fun onActive() {
        Log.d("App", "CurrentLocationListener onActive")
        googleApiClient.registerConnectionCallbacks(this)
        googleApiClient.connect()
    }

    @SuppressLint("MissingPermission")
    override fun onInactive() {
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient.disconnect()
        }
        googleApiClient.unregisterConnectionCallbacks(this)
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        Log.d("App", "CurrentLocationListener onConnected")
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        lastLocation?.let { value = it }

        if (hasActiveObservers() && googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("App", "Location changed to $location")
        location?.let { value = it }
    }

    override fun onConnectionSuspended(errorCode: Int) {
        Log.d(TAG, "Connection suspended with error code $errorCode")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // TODO expose this state as described here:
        // https://d.android.com/topic/libraries/architecture/guide.html#addendum
        Log.d(TAG, "Connection failed due to $connectionResult")
    }

    companion object {
        val TAG: String = CurrentLocationListener::class.java.simpleName
    }
}