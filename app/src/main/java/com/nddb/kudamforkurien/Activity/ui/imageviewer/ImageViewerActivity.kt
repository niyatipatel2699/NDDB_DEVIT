package com.nddb.kudamforkurien.Activity.ui.imageviewer

import android.os.Bundle
import com.bumptech.glide.Glide
import com.nddb.kudamforkurien.Activity.BaseActivity
import com.nddb.kudamforkurien.databinding.ActivityImageViewerBinding

import android.view.View
import androidx.appcompat.content.res.AppCompatResources

import com.bumptech.glide.request.RequestOptions
import com.nddb.kudamforkurien.R


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