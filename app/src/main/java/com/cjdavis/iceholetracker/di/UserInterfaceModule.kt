package com.cjdavis.iceholetracker.di

import android.arch.lifecycle.ViewModelProvider
import com.cjdavis.iceholetracker.ui.mapview.MapViewActivity
import com.cjdavis.iceholetracker.ui.mapview.di.MapViewModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UserInterfaceModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [(MapViewModule::class)])
    abstract fun contributeMapViewActivity(): MapViewActivity
}