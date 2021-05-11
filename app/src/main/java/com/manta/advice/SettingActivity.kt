package com.manta.advice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RadioButton
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
        createThemeSelectButtons(listOf(R.style.blackAndWhite, R.style.orange)).forEach {
            themeGroup.addView(it)
        }
    }

    private fun createThemeSelectButtons(themeList: List<Int>): List<RadioButton> {
        val buttons = mutableListOf<RadioButton>()
        for (theme in themeList) {
            RadioButton(this)
                .apply {
                    buttonDrawable = null
                    background = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_brightness_1_24, this@SettingActivity.theme)
                    layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    setOnClickListener {
                        application.setTheme(theme)
                    }
                    obtainStyledAttributes(theme, intArrayOf(android.R.attr.colorPrimary)).use {
                        backgroundTintList = ResourcesCompat.getColorStateList(
                            resources,
                            it.getResourceId(0, R.color.black),
                            this@SettingActivity.theme
                        )
                    }
                }.also { buttons.add(it) }
        }
        return buttons
    }
}
