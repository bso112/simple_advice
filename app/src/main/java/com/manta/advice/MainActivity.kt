package com.manta.advice

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.orange)
        super.onCreate(savedInstanceState)
        AdviceDialog( this).show(supportFragmentManager, "")

    }

    override fun onDismiss(dialog: DialogInterface?) {
        finish()
    }


}
