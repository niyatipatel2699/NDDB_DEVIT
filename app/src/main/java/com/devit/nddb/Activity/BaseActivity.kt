package com.devit.nddb.Activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.devit.nddb.NDDBApp
import com.devit.nddb.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { NDDBApp.getLocaleManager(base)?.setLocale(it) })
        Log.d("Language", "attachBaseContext")
    }

    fun showSnackBar(context: Context?, message: String?) {
        try {
            val view =
                (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            val snackBar =
                Snackbar.make(view, message!!, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
            val viewGroup = snackBar.view as ViewGroup
            viewGroup.setBackgroundColor(ContextCompat.getColor(context!!, R.color.black))
            val viewTv = snackBar.view
            val tv = viewTv.findViewById<TextView>(R.id.snackbar_text)
            tv.setTextColor(ContextCompat.getColor(context, R.color.white))
            tv.maxLines = 5
            snackBar.duration = 2000
            snackBar.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

  /*  fun showProgressDialog(ctx: Context) {
        try {
            customProgressDialog = CustomProgressDialog(ctx)
            customProgressDialog!!.show()
            customProgressDialog!!.setCancelable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgressDialog() {
        try {
            if (customProgressDialog != null && customProgressDialog!!.isShowing) {
                customProgressDialog!!.dismiss()
                customProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/
    val activity: Activity
        get() = this

}