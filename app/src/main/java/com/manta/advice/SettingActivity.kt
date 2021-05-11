package com.manta.advice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RadioButton
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.core.view.get
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
            themeGroup.setOnCheckedChangeListener { group, checkedId ->
                //TODO : 클릭한 라디오버튼 표시.
                group[checkedId]
            }
        }
    }

    private fun ActivitySettingBinding.addThemeSelectButtons() {
        createThemeSelectButtons(R.style.blackAndWhite, R.style.orange).forEach {
            themeGroup.addView(it)
        }
    }

    private fun createThemeSelectButtons(@StyleRes vararg themeList: Int): List<RadioButton> {
        val buttons = mutableListOf<RadioButton>()
        for (theme in themeList) {
            RadioButton(this)
                .apply {
                    buttonDrawable = null
                    background = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_brightness_1_24, null)
                    layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    setOnClickListener {
                        application.setTheme(theme)
                    }
                    //TODO : colorPrimary를 받아와야 되는데..
                    obtainStyledAttributes(theme, intArrayOf(android.R.attr.colorPrimary)).use {
                        Log.d("manta", "컬러 : " +  java.lang.Integer.toHexString(it.getColor(0, Color.BLACK)))

                    }
                }.also { buttons.add(it) }
        }
        return buttons
    }
}
