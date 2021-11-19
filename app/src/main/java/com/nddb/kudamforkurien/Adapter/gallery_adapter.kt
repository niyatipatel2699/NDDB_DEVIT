package com.nddb.kudamforkurien.Adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nddb.kudamforkurien.Activity.ui.imageviewer.ImageViewerActivity
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.model.GalleryData

class gallery_adapter(
    val context: Context,
    val languageData: List<GalleryData>
) : RecyclerView.Adapter<gallery_adapter.ViewHolder>() {


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        /*if(languageData[p1].attachmentType.equals("Image"))
        {
         *//*   val intent = Intent(context, ImageViewerActivity::class.java)
            intent.putExtra("imagePath",languageData[p1].attachmentPath)
            context.startActivity(intent)*//*

        }else {
            p0.imageViewBackground.setImageResource(R.drawable.video_img)
        }*/

      /*  Glide.with(p0.itemData)
            .load(languageData[p1].thumnail)
            .placeholder(R.drawable.app_icon)
            .fitCenter()
            .into(p0.imageViewBackground)*/

        //myImageView.setImageResource(myImageList[i]);
        p0.imageViewBackground.setImageResource(languageData[p1].thumnail)

        if(languageData[p1].attachmentType.equals("Video")){
            p0.videoIcon.visibility = View.VISIBLE
        }else{
            p0.videoIcon.visibility = View.GONE
        }

        p0.imageViewBackground.setOnClickListener {

            if(languageData[p1].attachmentType.equals("Image"))
            {
                val intent = Intent(context, ImageViewerActivity::class.java)
                intent.putExtra("imagePath",languageData[p1].attachmentPath)
                context.startActivity(intent)
            }
            else
            {
                if(p1 == 0){
                    watchYoutubeVideo(context,"dbAoX5dyIOI")
                }
                else if(p1==3)
                {
                    watchYoutubeVideo(context,"Me85wbcC0Zk")
                }
                else if(p1 == languageData.size - 1)
                {
                    watchYoutubeVideo(context,"DBtNPgYWjKU")
                }
                else
                {
                    watchYoutubeVideo(context,"EHg--XWNr9I")
                }

            }


        }
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
        lateinit var videoIcon : ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_pic)
            videoIcon = itemView.findViewById(R.id.iv_video)
            this.itemData = itemView
        }


    }

    fun watchYoutubeVideo(context: Context, id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$id")
        )
        try {
            context.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            context.startActivity(webIntent)
        }
    }
}