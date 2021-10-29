package com.devit.nddb.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.devit.nddb.R
import com.devit.nddb.data.remote.responses.Registration.UserTypeData

class UserTypeAdapter (val context: Context?, private val responseDataList: List<UserTypeData>?) : BaseAdapter() {

    override fun getCount(): Int {
        return responseDataList!!.size + 1
    }

    override fun getItem(position: Int): Any {
        return responseDataList!![position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var list: UserTypeData? = null
        try {
            if (position > 0) {
                list = responseDataList!![position - 1]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        @SuppressLint("ViewHolder")
        val view: View = LayoutInflater.from(context).inflate(R.layout.dropdown_menu, parent, false)
        val tvUserType: TextView = view.findViewById(R.id.textView)
        if (position == 0) {
            tvUserType.setText(R.string.user_type)
            tvUserType.isEnabled = false
            tvUserType.isClickable = false
            tvUserType.isFocusable = false
            tvUserType.setTextColor(ContextCompat.getColor(context!!, R.color.black))
        } else {
            if (list != null) tvUserType.text = list.name
            tvUserType.setTextColor(ContextCompat.getColor(context!!, R.color.black))
        }
        return view
    }
}