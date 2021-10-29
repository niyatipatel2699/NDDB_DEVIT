package com.devit.nddb.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devit.nddb.R

class walked_history_adapter(
    val context: Context,
    val languageData: List<String>
) : RecyclerView.Adapter<walked_history_adapter.ViewHolder>() {

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tv_Count?.text = languageData[p1]
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0.context).inflate(R.layout.adapter_walked_history_item, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return languageData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_Count = itemView.findViewById<TextView>(R.id.tv_Count)
    }

}