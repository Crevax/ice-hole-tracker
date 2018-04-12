package com.cjdavis.iceholetracker.di

import android.app.Application
import com.cjdavis.iceholetracker.IceHoleTrackerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AndroidInjectionModule::class),
    (AndroidSupportInjectionModule::class),
    (AppModule::class),
    (GoogleServicesModule::class),
    (ServiceModule::class),
    (UserInterfaceModule::class)
])
interface AppComponent : AndroidInjector<IceHoleTrackerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(app: IceHoleTrackerApplication)
}
