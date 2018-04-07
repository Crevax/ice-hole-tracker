package com.cjdavis.iceholetracker.ui

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    /**
     * Convenience method for running VM code in an activity's onCreate method
     * or a fragment's onActivityCreated method
     */
    open fun start() {}
}

/**
 * Note - this subclass of the application base view model
 * is currently only intended to be used by view models backing fragments
 * The base activities in the application do not call this method
 */
abstract class LifeCycleViewModel: BaseViewModel() {

    open fun startLoading(lifecycleOwner: LifecycleOwner) { /* this space for rent */}

}