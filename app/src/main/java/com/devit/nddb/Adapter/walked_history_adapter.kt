package com.devit.nddb.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devit.nddb.R
import com.wajahatkarim3.imagine.data.room.entity.Steps
import java.text.SimpleDateFormat

class walked_history_adapter(
    val context: Context,
    val stepslist: List<Steps>
) : RecyclerView.Adapter<walked_history_adapter.ViewHolder>() {

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tv_Count?.text = stepslist[p1].step.toString()
        val fDate: String = SimpleDateFormat("yyyy-MM-dd").format(stepslist[p1].date)
        p0.tvDate?.text = fDate
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0.context).inflate(R.layout.adapter_walked_history_item, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return stepslist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_Count = itemView.findViewById<TextView>(R.id.tv_Count)
        val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
    }

}