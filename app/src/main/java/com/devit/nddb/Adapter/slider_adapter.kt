package com.devit.nddb.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.devit.nddb.R
import com.devit.nddb.model.SliderData
import com.smarteist.autoimageslider.SliderViewAdapter


internal class slider_adapter(context: Context?, sliderDataArrayList: ArrayList<SliderData>) :
    SliderViewAdapter<slider_adapter.SliderAdapterViewHolder>() {
    private val mSliderItems: List<SliderData>

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, null)
        return SliderAdapterViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder, position: Int) {
        val sliderItem = mSliderItems[position]

        Glide.with(viewHolder.itemData)
            .load(sliderItem.imgUrl)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    internal class SliderAdapterViewHolder(itemView: View) : ViewHolder(itemView) {
        lateinit var itemData: View
        var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.myimage)
            this.itemData = itemView
        }
    }

    init {
        mSliderItems = sliderDataArrayList
    }
}