package com.infomantri.bhakti.bhajan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.*
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.data.model.YoutubeVideo
import com.infomantri.bhakti.bhajan.database.YoutubeVideoDB

class YoutubeVideosAdapter(
    context: Context,
    loadVideo: (Int) -> Unit
) :
    ListAdapter<YoutubeVideo, YoutubeVideosAdapter.YoutubeVideoViewHolder>(
        DIFF_UTIL
    ) {

    private val mContext = context
    private val mLoadVideo = loadVideo

    companion object {
        val YoutubeAPIKey = "AIzaSyA9y2AKALhQS8e7BlY1HFk72flEh60HDD8"

        val DIFF_UTIL = object : DiffUtil.ItemCallback<YoutubeVideo>() {
            override fun areItemsTheSame(
                oldItem: YoutubeVideo,
                newItem: YoutubeVideo
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: YoutubeVideo,
                newItem: YoutubeVideo
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class YoutubeVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItemView: TextView = itemView.findViewById(R.id.tvTitle)
        val youtubeThumbnailItemView: YouTubeThumbnailView =
            itemView.findViewById(R.id.youtubeThumbnailView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeVideoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_youtube_video_item, parent, false)

        return YoutubeVideoViewHolder(itemView)
    }

    var count = itemCount

    override fun onBindViewHolder(holder: YoutubeVideoViewHolder, position: Int) {

        val current = getItem(position)
        holder.titleItemView.text = current.title

//        Picasso.get()
//            .load(current.imageUrl)
//            .fit()
//            .centerInside()
//            .into(holder.thumbnailItemView)

        holder.itemView.setOnClickListener {
            mLoadVideo(position)
        }
    }

}