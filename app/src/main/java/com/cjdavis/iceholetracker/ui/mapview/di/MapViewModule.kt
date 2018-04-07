package com.cjdavis.iceholetracker.ui.mapview.di

import android.arch.lifecycle.ViewModel
import com.cjdavis.iceholetracker.di.ViewModelKey
import com.cjdavis.iceholetracker.ui.mapview.MapViewViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MapViewModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapViewViewModel::class)
    abstract fun bindMapViewViewModel(viewModel: MapViewViewModel): ViewModel
}