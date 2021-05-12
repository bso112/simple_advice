package com.manta.advice

import com.android.volley.toolbox.StringRequest

data class AppRequest(
    val apiName: String,
    val request: StringRequest
) {
    init {
        request.setShouldCache(false)
    }
}
