package com.manta.advice.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.preference.PreferenceManager
import com.manta.advice.utill.AppConstant.EXTRA_THEME_ID
import com.manta.advice.R
import com.manta.advice.databinding.ActivitySettingBinding
import com.manta.advice.dpToPx


class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding {
            themeGroup.addThemeSelectButtons()
            themeGroup.setOnCheckedChangeListener { group, checkedId ->
                //TODO : 클릭한 라디오버튼 표시.
                group.findViewById<RadioButton>(checkedId)
            }
        }

    }


    private fun RadioGroup.addThemeSelectButtons() {
        createThemeSelectButtons(R.style.blackAndWhite, R.style.orange).forEach {
            addView(it)
        }
    }


    private fun createThemeSelectButtons(@StyleRes vararg styleList: Int): List<RadioButton> {
        val buttons = mutableListOf<RadioButton>()
        for (styleResID in styleList) {
            RadioButton(this)
                .apply {
                    buttonDrawable = null
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_brightness_1_24,
                        null
                    )
                    width = dpToPx(50)
                    height = dpToPx(50)
                    setOnClickListener {
                        PreferenceManager.getDefaultSharedPreferences(this@SettingActivity)
                            .edit()
                            .putInt(EXTRA_THEME_ID, styleResID)
                            .apply()
                    }

                    //style.xml 파일에 있는 style을 파싱
                    obtainStyledAttributes(styleResID, intArrayOf(R.attr.colorPrimary)).use {
                       backgroundTintList = ColorStateList.valueOf(it.getColor(0, Color.BLACK))
                    }

                }.also { buttons.add(it) }
        }
        return buttons
    }
}
