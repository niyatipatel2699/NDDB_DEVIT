package com.devit.nddb.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devit.nddb.R

class gallery_adapter(
    val context: Context,
    val languageData: List<String>
) : RecyclerView.Adapter<gallery_adapter.ViewHolder>() {

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(p0.itemData)
            .load(languageData[p1])
            .fitCenter()
            .into(p0.imageViewBackground)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0.context).inflate(R.layout.adapter_gallery_item, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return languageData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var itemData: View
        var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_pic)
            this.itemData = itemView
        }


    }

}