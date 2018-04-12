package com.cjdavis.iceholetracker.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.cjdavis.iceholetracker.BR
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseActivity<TViewModel : BaseViewModel, TBinding : ViewDataBinding> :
        AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>

    protected abstract val viewModelClassToken: Class<TViewModel>
    protected abstract val layoutId: Int

    protected var binding: TBinding? = null
    protected val vm by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(viewModelClassToken)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId)
        binding?.setVariable(BR.vm, vm)
        binding?.setLifecycleOwner(this)

        subscribeUI()
        vm.start()
    }

    override fun onDestroy() {
        binding = null

        super.onDestroy()
    }

    // Keeps implementation's onCreate cleaner, while ensuring LiveData subscriptions happen there
    protected open fun subscribeUI() {}
}
