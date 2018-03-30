package com.cjdavis.iceholetracker

import com.cjdavis.iceholetracker.di.DaggerAppComponent
import dagger.android.DaggerApplication

class IceHoleTrackerApplication : DaggerApplication() {

    override fun applicationInjector() = DaggerAppComponent
            .builder()
            .application(this)
            .build()
}