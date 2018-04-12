package com.cjdavis.iceholetracker.di

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GoogleServicesModule {

    @Provides
    @Singleton
    fun provideGoogleApiClient(context: Context) = GoogleApiClient
            .Builder(context)
            .addApi(LocationServices.API)
            .build()
}
