package com.manta.advice

import android.content.Context
import android.util.TypedValue
import androidx.preference.PreferenceManager
import com.manta.advice.utill.AppConstant

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
}

fun Context.loadTheme(){
    PreferenceManager
        .getDefaultSharedPreferences(this)
        .getInt(AppConstant.EXTRA_THEME_ID, 0)
        .takeIf { it != 0 }
        ?.also {
            setTheme(it)
        }
}