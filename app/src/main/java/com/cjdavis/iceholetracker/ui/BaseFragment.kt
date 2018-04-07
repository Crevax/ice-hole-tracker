package com.cjdavis.iceholetracker.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Inject
import com.cjdavis.iceholetracker.BR
import com.cjdavis.iceholetracker.di.Injectable

abstract class BaseFragment<TViewModel: BaseViewModel, TBinding: ViewDataBinding>
    : ViewLifeCycleFragment(), Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelClassToken: Class<TViewModel>
    protected abstract val layoutId: Int

    protected var binding: TBinding? = null
    protected open lateinit var vm: TViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = provideViewModel()
        vm.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            DataBindingUtil.inflate<TBinding>(inflater, layoutId, container, false).also {
                it.setVariable(BR.vm, vm)
                binding = it
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (vm as? LifeCycleViewModel)?.startLoading(lifeCycleOwner)
    }

    override fun onDestroy() {
        binding = null

        super.onDestroy()
    }

    /**
     * provideViewModel
     * is an optionally override-able method in the event a subclass needs to return a special instance of
     * it's respective view model.
     *
     * The default behavior is to use the lifecycle scope of the fragment, but could be
     * overridden to in the event a fragment needs to use its parent activity scope, etc
     */
    protected open fun provideViewModel(): TViewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClassToken)
}