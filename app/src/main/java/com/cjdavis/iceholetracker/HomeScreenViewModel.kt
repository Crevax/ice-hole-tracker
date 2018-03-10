package com.cjdavis.iceholetracker

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class HomeScreenViewModel : ViewModel() {

    var userMsg = MutableLiveData<String>()

    var holeDepth = ObservableField<String>()
    var notes = ObservableField<String>()

    fun start() {}

    fun sendGPSCoordinates() {
        // TODO: Call Intent for Email app and attach file to new email
        userMsg.value = "Not implemented yet!"
    }

    fun resetInput() {
        holeDepth.set("")
        notes.set("")
    }
}