package com.manta.advice.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.manta.advice.AppRequest
import com.manta.advice.databinding.DialogAdviceBinding
import com.manta.advice.utill.APIKeyStore
import org.json.JSONObject
import kotlin.random.Random


class AdviceDialog() : DialogFragment() {

    val isConfigShow = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    private var isAlreadyTranslated = false
    private var oldActiveRequest = emptyList<AppRequest>()


    private val binding: DialogAdviceBinding by lazy {
        DialogAdviceBinding.inflate(layoutInflater)
    }

    val requests: List<AppRequest> by lazy {
        createAdviceRequest { content, author ->
            binding.progress.visibility = View.INVISIBLE
            var contentWithAuthor = content
            author?.let {
                contentWithAuthor += "\n- $it"
            }

            binding.advice.text = contentWithAuthor

            binding.card.setOnClickListener {
                if (isConfigShow.value == true)
                    return@setOnClickListener

                if (isAlreadyTranslated || getActiveRequests().isEmpty()) {
                    dismiss()
                    return@setOnClickListener
                }

                isLoading.value = true

                requestTranslation(contentWithAuthor) { translated ->
                    isAlreadyTranslated = true
                    isLoading.value = false
                    binding.advice.text = translated
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.dialog = this
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
        }


        requestAdvice()


        binding.card.setOnClickListener {
            if (getActiveRequests().isEmpty())
                dismiss()
        }

        binding.card.setOnLongClickListener {
            isConfigShow.value = true
            //copyToClipboard(contentWithAuthor)
            true
        }

        binding.btnBack.setOnClickListener {
            isConfigShow.value = false
            if (isRequestConfigChanged())
                requestAdvice()
        }

        binding.btnSetting.setOnClickListener {
            Intent(requireActivity(), SettingActivity::class.java).apply{
                startActivity(this)
            }
        }

        isLoading.observe(requireActivity()) {
            if (it) {
                binding.advice.text = ""
            }
        }

        isConfigShow.observe(requireActivity()) {
            if (it) {
                //이전설정 저장
                oldActiveRequest = getActiveRequests()
            }
        }
    }



    private fun isRequestConfigChanged(): Boolean {
        getActiveRequests().run {
            if (size != oldActiveRequest.size) return true
            forEachIndexed { i, request -> if (request.apiName != oldActiveRequest[i].apiName) return@isRequestConfigChanged true }
        }
        return false
    }


    private fun copyToClipboard(contentWithAuthor: String) {
        val clipboardMgr = getSystemService(requireContext(), ClipboardManager::class.java)
        clipboardMgr?.setPrimaryClip(ClipData.newPlainText(null, contentWithAuthor))
        Toast.makeText(requireContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getActiveRequests() = requests.filter {
        requireContext().getSharedPreferences("api", MODE_PRIVATE).getBoolean(it.apiName, false)
    }

    private fun requestAdvice() {
        isAlreadyTranslated = false
        val queue = Volley.newRequestQueue(context)
        val activeRequests = getActiveRequests()

        if (activeRequests.isEmpty()) {
            binding.advice.text = "길게 눌러서 표시할 항목을 선택하세요."
            return
        }

        val randomRequestIndex = Random.nextInt(activeRequests.count())
        queue.add(activeRequests[randomRequestIndex].request)
        isLoading.value = true

    }

    private fun requestTranslation(toTranslate: String, onTranslate: (String) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val request = object :
            StringRequest(Method.POST, "https://openapi.naver.com/v1/papago/n2mt", {
                val result = JSONObject(it).getJSONObject("message").getJSONObject("result")
                    .getString("translatedText")
                onTranslate(result)
            }, {
                if (it.networkResponse.statusCode == 429)
                    Toast.makeText(context, "파파고 번역 서비스의 이용한도를 초과했습니다.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "source" to "en",
                    "target" to "ko",
                    "text" to toTranslate
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "X-Naver-Client-Id" to APIKeyStore.getNaverClientID(),
                    "X-Naver-Client-Secret" to APIKeyStore.getNaverSecret()
                )

            }
        }
        queue.add(request)

    }

    private fun createAdviceRequest(onReceiveAdvice: (advice: String, author: String?) -> Unit): List<AppRequest> {
        val errorListener = Response.ErrorListener {
            Toast.makeText(requireContext(), "서버 오류가 발생했습니다!", Toast.LENGTH_SHORT).show()
        }

        val adviceRequest =
            StringRequest(Request.Method.GET, "https://api.adviceslip.com/advice", { adviceJson ->
                val advice = JSONObject(adviceJson).getJSONObject("slip").getString("advice")
                onReceiveAdvice(advice, null)
            }, errorListener)

        //페쇄된듯?
//        val quoteRequest =
//            StringRequest(Request.Method.GET, "https://favqs.com/api/qotd", { quoteJson ->
//                val quote = JSONObject(quoteJson).getJSONObject("quote")
//                val advice = quote.getString("body")
//                val author = quote.getString("author")
//                onReceiveAdvice(advice, author)
//            }, {})

        val jokeRequest =
            object : StringRequest(Request.Method.GET, "https://icanhazdadjoke.com/", { jokeJson ->
                val joke = JSONObject(jokeJson).getString("joke")
                onReceiveAdvice(joke, null)
            }, errorListener) {
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
            }, errorListener)

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
                errorListener)


        return listOf(
            AppRequest("생활속 조언", adviceRequest),
            //AppRequest("명언", quoteRequest),
            AppRequest("아재 개그", jokeRequest),
            AppRequest("칸예 어록", kanyeRequest),
            AppRequest("명언2(느린서버)", quotRequest2)
        )
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (requireActivity() as DialogInterface.OnDismissListener).onDismiss(dialog)
    }

}