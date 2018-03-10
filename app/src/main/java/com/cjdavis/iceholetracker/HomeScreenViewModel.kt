package com.cjdavis.iceholetracker

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class HomeScreenViewModel : ViewModel() {

    var userMsg = MutableLiveData<String>()

    fun start() {}

    fun sendGPSCoordinates() {
        // TODO: Call Intent for Email app and attach file to new email
        userMsg.value = "Not implemented yet!"
    }
}