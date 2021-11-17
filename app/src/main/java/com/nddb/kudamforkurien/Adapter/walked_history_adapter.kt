package com.nddb.kudamforkurien.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.remote.responses.History.HistoryData
import com.nddb.kudamforkurien.data.room.entity.Steps
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class walked_history_adapter(
    val context: Context,
    val stepslist: List<HistoryData>
) : RecyclerView.Adapter<walked_history_adapter.ViewHolder>() {

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tv_Count?.text = stepslist[p1].steps.toString()
        //val fDate: String = stepslist[p1].date
        /*val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val inputDateStr = stepslist[p1].date
        val date: Date = inputFormat.parse(inputDateStr)
        val outputDateStr: String = outputFormat.format(date)*/
        p0.tvDate?.text = stepslist[p1].date
        p0.tvRank?.text = stepslist[p1].rnk.toString()
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
        var tvRank = itemView.findViewById<TextView>(R.id.tv_rank)
    }

}