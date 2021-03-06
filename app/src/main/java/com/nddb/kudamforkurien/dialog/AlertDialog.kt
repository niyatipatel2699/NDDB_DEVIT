package com.nddb.kudamforkurien.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatButton

import androidx.appcompat.widget.AppCompatTextView
import com.nddb.kudamforkurien.R
import java.util.*


class AlertDialog(context: Context?,type: String) : Dialog(context!!) {
    var txtTitle: AppCompatTextView
    var btnOk: AppCompatButton

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.alert_dialog)
        val v: View =
            Objects.requireNonNull(getWindow())!!.getDecorView()
        v.setBackgroundResource(R.color.transparent)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        btnOk = findViewById(R.id.btnOk)
        txtTitle = findViewById(R.id.txt_title)

        if(type.equals("home"))
        {
            txtTitle.text=context?.getString(R.string.thank_you_for_using_app)
        }


    }
}