package com.cjdavis.iceholetracker.ui.mapview

import android.databinding.ObservableField
import android.os.Environment
import android.util.Log
import com.cjdavis.iceholetracker.service.CurrentLocationListener
import com.cjdavis.iceholetracker.ui.BaseViewModel
import com.cjdavis.iceholetracker.util.SingleLiveEvent
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject

class MapViewViewModel @Inject constructor() : BaseViewModel() {

    @Inject lateinit var currentLocation: CurrentLocationListener

    var userMsg = SingleLiveEvent<String>()

    var holeDepth = ObservableField<String>()
    var notes = ObservableField<String>()

    private lateinit var directory: File
    private lateinit var records: File

    private val isStorageReadable: Boolean
    get() {
        val storageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == storageState || Environment.MEDIA_MOUNTED_READ_ONLY == storageState
    }

    private val isStorageWritable: Boolean
    get() {
        val storageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == storageState
    }

    private val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val MIN_ACCURACY = 20.0f

    fun sendGPSCoordinates() {
        // TODO: Call Intent for Email app and attach file to new email
        userMsg.value = "Not implemented yet! ${currentLocation.value}"
    }

    fun saveGPSCoordinates() {
        currentLocation.value?.let {
            // TODO: compare location.getTime(), and if it's too old (we'll determine that later), request a location update)
            try {
                // TODO: Find a better method of saving the file
                val writer = FileWriter(records, true)
                writer.write(String.format("%1\$s,%2\$s,%3\$s,%4\$s,%5\$s,%6\$s,\"%7\$s\"\n",
                        sdfDate.format(System.currentTimeMillis()),
                        holeDepth.get(),
                        it.latitude,
                        it.longitude,
                        it.altitude,
                        it.accuracy,
                        notes.get()))
                writer.close()

                userMsg.value = "Saved to file!"
            } catch (ex: IOException) {
                Log.e(TAG, ex.message)
            }

            resetInput()
        } ?: run {
            userMsg.value =  "Waiting for location"
        }
    }

    fun checkFile() {
        // TODO: Find out the proper way to create this file in public store
        if (isStorageReadable && isStorageWritable) {
            directory = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME
            )
            if (!directory.exists() && !directory.mkdirs()) {
                userMsg.value = "Unable to create directory for file storage"
            } else {
                records = File(directory.path, FILE_NAME)
                if (!records.exists()) {
                    try {
                        records.createNewFile()
                        val writer = FileWriter(records)
                        writer.write("timestamp,depth,latitude,longitude,altitude,accuracy,notes\n")
                        writer.close()
                    } catch (ex: IOException) {
                        Log.e(TAG, ex.message)
                        userMsg.value = "Error saving data. Check log for more details."
                    }

                }
            }
        } else {
            Log.e(TAG, "File system is not writable")
            userMsg.value = "File not readable/writable. Check log for more details."
        }
    }

    private fun resetInput() {
        holeDepth.set("")
        notes.set("")
    }

    companion object {
        val TAG: String = MapViewViewModel::class.java.simpleName

        private const val FOLDER_NAME = "IceHoleTracker"
        private const val FILE_NAME = "saved-depths.csv"
    }
}