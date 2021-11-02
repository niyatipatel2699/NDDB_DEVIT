package com.devit.nddb.Activity.ui.imageviewer

import android.os.Bundle
import com.bumptech.glide.Glide
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.databinding.ActivityImageViewerBinding
import android.graphics.Bitmap

import com.bumptech.glide.request.target.SimpleTarget

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources

import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.devit.nddb.R


class ImageViewerActivity  : BaseActivity()
{
    lateinit var regBinding: ActivityImageViewerBinding

    lateinit var imagePath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regBinding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(regBinding.root)

        imagePath = intent.getStringExtra("imagePath").toString()
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.app_icon)

       /* Glide.with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(url).into<Target<Drawable>>(holder.imageView)
        */
     /*   Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(imagePath)
            .dontAnimate()
            .into(regBinding.imageSingle)
*/
        Glide.with(this)
            .load(imagePath)
            .placeholder(AppCompatResources.getDrawable(regBinding.imageSingle.getContext(), R.drawable.app_icon))
            .dontAnimate()
            .into(regBinding.imageSingle);


    }
}