package com.cjdavis.iceholetracker.di

import android.app.Application
import android.content.Context
import com.cjdavis.iceholetracker.AppLifecycleCallbacks
import com.cjdavis.iceholetracker.IceHoleTrackerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppLifecycleCallbacks(): AppLifecycleCallbacks = ReleaseAppLifecycleCallbacks()

    @Singleton
    @Provides
    fun provideIceHoleTrackerApplication(application: Application): IceHoleTrackerApplication
        = (application as IceHoleTrackerApplication)

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}