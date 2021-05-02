package com.manta.advice

import android.app.Activity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onReceiveAdvise { advice ->
            AdviceDialog(this, advice).apply {
                setOnDismissListener {
                    finish()
                }
                show()
            }
        }


    }

    private fun onReceiveAdvise(onReceiveAdvise: (String) -> Unit) {
        val queue = Volley.newRequestQueue(this)
        val request =
            StringRequest(Request.Method.GET, "https://api.adviceslip.com/advice", { adviceJson ->
                val advice = JSONObject(adviceJson).getJSONObject("slip").getString("advice")
                onReceiveAdvise(advice)
            }, {})
        request.setShouldCache(false)
        queue.add(request)
    }
}
