package com.manta.advice.ui

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val contentLayoutId : Int
) : AppCompatActivity() {


    protected val binding : T by lazy{
        DataBindingUtil.setContentView<T>(this, contentLayoutId)
    }


    protected inline fun binding(block: T.() -> Unit): T {
        return binding.apply(block)
    }


}