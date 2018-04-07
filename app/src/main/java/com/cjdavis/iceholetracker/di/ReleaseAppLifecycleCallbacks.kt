package com.cjdavis.iceholetracker.di

import com.cjdavis.iceholetracker.AppLifecycleCallbacks
import com.cjdavis.iceholetracker.IceHoleTrackerApplication

class ReleaseAppLifecycleCallbacks : AppLifecycleCallbacks {
    override fun onCreate(app: IceHoleTrackerApplication) {}
    override fun onTerminate(app: IceHoleTrackerApplication) {}
}
