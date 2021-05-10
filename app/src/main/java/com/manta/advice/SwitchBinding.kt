package com.manta.advice

import android.content.Context.MODE_PRIVATE
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter

object SwitchBinding {

    @JvmStatic
    @BindingAdapter("apiName")
    fun SwitchCompat.apiName(apiName: String) {
        val preference = context.getSharedPreferences("api", MODE_PRIVATE)
        isChecked = preference.getBoolean(apiName, false)
        setOnCheckedChangeListener { _, isChecked ->
            preference.edit().putBoolean(apiName, isChecked).apply()
        }
    }
}