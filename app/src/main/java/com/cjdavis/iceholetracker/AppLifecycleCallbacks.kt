package com.cjdavis.iceholetracker

interface AppLifecycleCallbacks {
    fun onCreate(app: IceHoleTrackerApplication)
    fun onTerminate(app: IceHoleTrackerApplication)
}