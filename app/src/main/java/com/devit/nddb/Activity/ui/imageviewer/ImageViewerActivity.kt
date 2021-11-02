package com.devit.nddb.Activity.ui.imageviewer

import android.os.Bundle
import com.bumptech.glide.Glide
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.databinding.ActivityImageViewerBinding

class ImageViewerActivity  : BaseActivity()
{
    lateinit var regBinding: ActivityImageViewerBinding

    lateinit var imagePath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regBinding = ActivityImageViewerBinding.inflate(layoutInflater)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            imagePath = bundle.getString("imagePath").toString()

        }

        if (this::imagePath.isInitialized)
        {
            Glide.with(this)
                .load(imagePath)
                .fitCenter()
                .into(regBinding.imageSingle)
        }


    }
}