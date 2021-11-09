package com.devit.nddb.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.data.remote.responses.Language.LanguageData

class CustomRecyclerAdapter(
    val context: Context,
    val languageData: List<String>,
    val type: Int
) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    var row_index = -1



    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.txtTitle?.text = languageData[p1]
        var langId= MySharedPreferences.getMySharedPreferences()!!.lang_id
        /* for (i in languageData.indices) {
             if(langId==languageData[i].id)
             {
                 row_index=i
             }
         }*/



        if (row_index == p1) {
            p0.cardView.setCardBackgroundColor(Color.parseColor("#CA751B"))
            p0.txtTitle.setTextColor(Color.parseColor("#ffffff"))
        } else {
            p0.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"))
            p0.txtTitle.setTextColor(Color.parseColor("#000000"))

        }
        if(type==2)
        {
            if (row_index == -1) {
                if (langId.equals(languageData[p1])) {
                    p0.cardView.setCardBackgroundColor(Color.parseColor("#CA751B"))
                    p0.txtTitle.setTextColor(Color.parseColor("#ffffff"))
                } else {
                    p0.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"))
                    p0.txtTitle.setTextColor(Color.parseColor("#000000"))

                }
            }
        }
        p0.cardView!!.setOnClickListener {
            selectPostion(p1)
        }

    }

    fun selectPostion(pos:Int)
    {
        row_index = pos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.grid_items, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return languageData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView.findViewById<TextView>(R.id.tv_language)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
    }

}

