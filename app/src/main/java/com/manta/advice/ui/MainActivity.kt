package com.manta.advice.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manta.advice.loadTheme

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       AdviceDialog().show(supportFragmentManager, "")

    }

    override fun onDismiss(dialog: DialogInterface?) {
        finish()
    }


}
