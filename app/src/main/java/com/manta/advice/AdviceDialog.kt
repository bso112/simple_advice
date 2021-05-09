package com.manta.advice

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.manta.advice.databinding.DialogAdviceBinding
import org.json.JSONObject
import kotlin.random.Random


class AdviceDialog(context: Context, private val lifecycleOwner: LifecycleOwner) : Dialog(context) {

    val isConfigShow = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    private var isAlreadyTranslated = false
    private var oldActiveRequest = emptyList<AppRequest>()


    private val binding: DialogAdviceBinding by lazy {
        DialogAdviceBinding.inflate(layoutInflater)
    }

    val requests: List<AppRequest> by CreateAdviceRequestDelegate { content, author ->
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.dialog = this
        binding.lifecycleOwner = lifecycleOwner
        setContentView(binding.root)

        window?.setBackgroundDrawableResource(android.R.color.transparent);
        window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT

       // val s = getNaverClientID()

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

        isLoading.observe(lifecycleOwner) {
            if (it) {
                binding.advice.text = ""
            }
        }

        isConfigShow.observe(lifecycleOwner) {
            if (it) {
                //이전설정 저장
                oldActiveRequest = getActiveRequests()
            }
        }
    }


    private fun isRequestConfigChanged(): Boolean {
        getActiveRequests().run {
            if (size != oldActiveRequest.size) return true
            forEachIndexed { i, e -> if (e.apiName != oldActiveRequest[i].apiName) return@isRequestConfigChanged true }
        }
        return false
    }


    private fun copyToClipboard(contentWithAuthor: String) {
        val clipboardMgr = getSystemService(context, ClipboardManager::class.java)
        clipboardMgr?.setPrimaryClip(ClipData.newPlainText(null, contentWithAuthor))
        Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getActiveRequests() = requests.filter {
        context.getSharedPreferences("api", MODE_PRIVATE).getBoolean(it.apiName, false)
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


}