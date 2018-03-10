package com.cjdavis.iceholetracker

import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat

class HomeScreen : FragmentActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var directory: File? = null
    private var records: File? = null
    private var edtHoleDepth: EditText? = null
    private var edtNotes: EditText? = null
    private var btnSubmit: Button? = null

    private val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val isStorageReadable: Boolean
        get() {
            val storageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == storageState || Environment.MEDIA_MOUNTED_READ_ONLY == storageState

        }

    val isStorageWritable: Boolean
        get() {
            val storageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == storageState

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        edtHoleDepth = findViewById<View>(R.id.edtHoleDepth) as EditText
        edtNotes = findViewById<View>(R.id.edtNotes) as EditText
        btnSubmit = findViewById<View>(R.id.btnSubmit) as Button

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong())
                .setFastestInterval((1 * 1000).toLong())

        if (!checkFile()) {
            Toast.makeText(applicationContext, "Error with file storage. Check log for more details",
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient!!.connect()
        btnSubmit!!.isEnabled = false

        if (!checkFile()) {
            Toast.makeText(applicationContext, "Error with file storage. Check log for more details",
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()

        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(i: Int) {

    }

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
        btnSubmit!!.isEnabled = location.accuracy <= MIN_ACCURACY
    }

    fun GetGPSCoordinates(v: View) {
        val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (location == null) {
            Toast.makeText(applicationContext, "Waiting for location", Toast.LENGTH_LONG)
                    .show()
        } else {
            // TODO: compare location.getTime(), and if it's too old (we'll determine that later), request a location update)
            try {
                // TODO: Find a better method of saving the file
                val writer = FileWriter(records!!, true)
                writer.write(String.format("%1\$s,%2\$s,%3\$s,%4\$s,%5\$s,%6\$s,\"%7\$s\"\n",
                        sdfDate.format(System.currentTimeMillis()),
                        edtHoleDepth!!.text,
                        location.latitude,
                        location.longitude,
                        location.altitude,
                        location.accuracy,
                        edtNotes!!.text))
                writer.close()
                Toast.makeText(applicationContext, "Saved to file!", Toast.LENGTH_LONG)
                        .show()
            } catch (ex: IOException) {
                Log.e(TAG, ex.message)
            }

            edtHoleDepth!!.setText("")
            edtNotes!!.setText("")
        }
    }

    fun SendGPSCoordinates(v: View) {
        // TODO: Call Intent for Email app and attach file to new email
        Toast.makeText(applicationContext, "Not implemented yet!",
                Toast.LENGTH_LONG).show()
    }

    private fun checkFile(): Boolean {
        // TODO: Find out the proper way to create this file in public store
        if (isStorageReadable && isStorageWritable) {
            directory = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME
            )
            if (!directory!!.exists() && !directory!!.mkdirs()) {
                Log.e(TAG, "Unable to create directory for file storage")
                return false
            } else {
                records = File(directory!!.path, FILE_NAME)
                if (!records!!.exists()) {
                    try {
                        records!!.createNewFile()
                        val writer = FileWriter(records!!)
                        writer.write("timestamp,depth,latitude,longitude,altitude,accuracy,notes\n")
                        writer.close()
                    } catch (ex: IOException) {
                        Log.e(TAG, ex.message)
                        return false
                    }

                }
            }
        } else {
            Log.e(TAG, "File system is not writable")
            return false
        }

        return true
    }

    companion object {

        // TODO: Rewrite App so everything doesn't happen in one activity
        val TAG = HomeScreen::class.java.simpleName

        /*
         * Define a request code to send to Google Play services
         * This code is returned in Activity.onActivityResult
         */
        private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        private val MIN_ACCURACY = 20.0f
        private val FOLDER_NAME = "IceHoleTracker"
        private val FILE_NAME = "saved-depths.csv"
    }
}
