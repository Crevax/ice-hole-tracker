package com.cjdavis.iceholetracker.di

import android.app.Application
import com.cjdavis.iceholetracker.IceHoleTrackerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class)])
interface AppComponent : AndroidInjector<IceHoleTrackerApplication> {

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application) : Builder
        fun build() : AppComponent
    }


    override fun inject(iceHoleTrackerApplication: IceHoleTrackerApplication)
}
