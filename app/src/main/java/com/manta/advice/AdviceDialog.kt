package com.manta.advice

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AdviceDialog(context: Context, private val advice: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_advice)

        window?.setBackgroundDrawableResource(android.R.color.transparent);

        window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        findViewById<CardView>(R.id.card).setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.advice).text = advice

    }

}