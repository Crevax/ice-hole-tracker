package com.cjdavis.iceholetracker

import com.cjdavis.iceholetracker.di.DaggerAppComponent
import com.cjdavis.iceholetracker.di.applyAutoInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class IceHoleTrackerApplication : DaggerApplication() {

    @Inject lateinit var appLifecycleCallbacks: AppLifecycleCallbacks

    override fun applicationInjector() = DaggerAppComponent
            .builder()
            .application(this)
            .build()

    override fun onCreate() {
        super.onCreate()
        applyAutoInjector()
        appLifecycleCallbacks.onCreate(this)
    }

    override fun onTerminate() {
        appLifecycleCallbacks.onTerminate(this)
        super.onTerminate()
    }
}