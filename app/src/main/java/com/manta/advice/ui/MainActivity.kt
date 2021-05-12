package com.manta.advice.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manta.advice.loadTheme

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
       // AdviceDialog().show(supportFragmentManager, "")
        Intent(this, SettingActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        finish()
    }


}
