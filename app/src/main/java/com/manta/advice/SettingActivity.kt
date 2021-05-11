package com.manta.advice

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.core.view.get
import androidx.core.view.size
import com.manta.advice.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    val binding: ActivitySettingBinding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        binding.apply {
            addThemeSelectButtons()
            Log.d("manta", "라디오버튼 그룹 사이즈 : " + themeGroup.size)
            themeGroup.setOnCheckedChangeListener { group, checkedId ->
                //TODO : 클릭한 라디오버튼 표시.
                group[checkedId]
            }
        }
    }

    private fun ActivitySettingBinding.addThemeSelectButtons() {
        createThemeSelectButtons(R.style.blackAndWhite, R.style.orange).forEach {
            themeGroup.addView(it)
            Log.d("manta", "라디오버튼 그룹 사이즈 : " + themeGroup.size)
        }
    }

    private fun createThemeSelectButtons(@StyleRes vararg styleList: Int): List<RadioButton> {
        val buttons = mutableListOf<RadioButton>()
        for (styleResID in styleList) {
            RadioButton(this)
                .apply {
                    buttonDrawable = null
                    background = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_brightness_1_24, null)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    setOnClickListener {
                        application.setTheme(styleResID)
                    }

                    //style.xml 파일에 있는 style을 파싱
                    obtainStyledAttributes(styleResID, intArrayOf(R.attr.colorPrimary)).use {
                        Log.d("manta", "라디오버튼 컬러 : " + Integer.toHexString(it.getColor(0, Color.BLACK)))
                        setBackgroundColor(it.getColor(0, Color.BLACK))
                    }

                }.also { buttons.add(it) }
        }
        return buttons
    }
}
