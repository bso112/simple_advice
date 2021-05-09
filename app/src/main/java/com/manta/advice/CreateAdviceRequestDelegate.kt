package com.manta.advice

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class AppRequest(
    val apiName: String,
    val request: StringRequest,
) {
    init {
        request.setShouldCache(false)
    }
}

class CreateAdviceRequestDelegate(
    private val onReceiveAdvice : (advice : String, author : String?)->Unit
) : ReadOnlyProperty<AdviceDialog, List<AppRequest>> {
    override fun getValue(
        thisRef: AdviceDialog,
        property: KProperty<*>
    ): List<AppRequest> {
        val adviceRequest =
            StringRequest(Request.Method.GET, "https://api.adviceslip.com/advice", { adviceJson ->
                val advice = JSONObject(adviceJson).getJSONObject("slip").getString("advice")
                onReceiveAdvice(advice, null)
            }, {})

        val quoteRequest =
            StringRequest(Request.Method.GET, "https://favqs.com/api/qotd", { quoteJson ->
                val quote = JSONObject(quoteJson).getJSONObject("quote")
                val advice = quote.getString("body")
                val author = quote.getString("author")
                onReceiveAdvice(advice, author)
            }, {})

        val jokeRequest =
            object : StringRequest(Request.Method.GET, "https://icanhazdadjoke.com/", { jokeJson ->
                val joke = JSONObject(jokeJson).getString("joke")
                onReceiveAdvice(joke, null)
            }, {}) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf(
                        "Accept" to "application/json"
                    )
                }
            }

        val kanyeRequest =
            StringRequest(Request.Method.GET, "https://api.kanye.rest/", { quoteJson ->
                val quote = JSONObject(quoteJson).getString("quote")
                onReceiveAdvice(quote, "Kanye Omari West")
            }, {})

        //서버가 좀 느림
        val quotRequest2 =
            StringRequest(
                Request.Method.GET,
                "https://quote-garden.herokuapp.com/api/v3/quotes/random",
                { quoteJson ->
                    val quote = JSONObject(quoteJson).getJSONArray("data").getJSONObject(0)
                    val advice = quote.getString("quoteText")
                    val author = quote.getString("quoteAuthor")
                    onReceiveAdvice(advice, author)
                },
                {})


        return listOf(
            AppRequest("생활속 조언", adviceRequest),
            AppRequest("명언", quoteRequest),
            AppRequest("아재 개그", jokeRequest),
            AppRequest("칸예 어록", kanyeRequest),
            AppRequest("명언2(느린서버)", quotRequest2)
        )
    }

}