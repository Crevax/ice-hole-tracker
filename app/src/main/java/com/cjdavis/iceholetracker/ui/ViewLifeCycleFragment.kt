package com.cjdavis.iceholetracker.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 *
 * This ViewLifecycle class is inspired by the following article
 * https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
 *
 * This manually implements and provides a lifecycle observer to avoid subtle bugs that may arise
 * from observing live data within fragments
 */
abstract class ViewLifeCycleFragment : Fragment() {

    private var viewLifecycleOwner: ViewLifecycleOwner? = null
    val lifeCycleOwner: LifecycleOwner
    //TODO: determine a better approach to handle this. Currently, this will only be null if the application is using a headless fragment
        get() = viewLifecycleOwner!!


    private class ViewLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        override fun getLifecycle(): LifecycleRegistry {
            return lifecycleRegistry
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner = ViewLifecycleOwner()
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroyView() {
        viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewLifecycleOwner = null
        super.onDestroyView()
    }

    // Keeps implementation's onCreate cleaner, while ensuring LiveData subscriptions happen there
    protected open fun subscribeUI() {}
}
