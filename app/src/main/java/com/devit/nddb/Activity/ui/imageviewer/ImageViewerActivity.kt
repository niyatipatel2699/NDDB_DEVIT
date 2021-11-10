package com.devit.nddb.Activity.ui.imageviewer

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.databinding.ActivityImageViewerBinding
import android.graphics.Bitmap

import com.bumptech.glide.request.target.SimpleTarget

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources

import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.devit.nddb.Activity.ui.gallery.GalleryFragment
import com.devit.nddb.Activity.ui.login.LoginActivity
import com.devit.nddb.R


class ImageViewerActivity  : BaseActivity()
{
    lateinit var regBinding: ActivityImageViewerBinding

     var imagePath:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regBinding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(regBinding.root)

        regBinding.toolbar.tvToolbartitle.visibility = View.GONE
        regBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        regBinding.toolbar.ivBack.setOnClickListener {

            finish()
        }

        imagePath = intent.getIntExtra("imagePath",0)
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