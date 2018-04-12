package com.cjdavis.iceholetracker.di

import com.cjdavis.iceholetracker.service.CurrentLocationListener
import com.google.android.gms.common.api.GoogleApiClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {

    @Singleton
    @Provides
    fun provideCurrentLocationListener(googleApiClient: GoogleApiClient)
            = CurrentLocationListener(googleApiClient)
}