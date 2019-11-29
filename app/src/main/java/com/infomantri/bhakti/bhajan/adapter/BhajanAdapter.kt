package com.infomantri.bhakti.bhajan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.data.model.Bhajan

class BhajanAdapter(text: (String) -> Unit, playVideo: (String) -> Unit) :
    ListAdapter<Bhajan, BhajanAdapter.BhajanViewHolder>(
        DIFF_UTIL
    ) {

    val mText = text
    val mPlayVideo = playVideo

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Bhajan>() {
            override fun areItemsTheSame(
                oldItem: Bhajan,
                newItem: Bhajan
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Bhajan,
                newItem: Bhajan
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class BhajanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItemView: TextView = itemView.findViewById(R.id.tvBhajanTitle)
        val thumbNailItemView: ImageView = itemView.findViewById(R.id.ivBhajanThumbnail)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BhajanViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_bhajan_item, parent, false)
        return BhajanViewHolder(itemView)
    }

    var count = itemCount

    override fun onBindViewHolder(holder: BhajanViewHolder, position: Int) {

        val current = getItem(position)
        holder.titleItemView.text = current.title

        holder.titleItemView.setOnClickListener { mText(holder.titleItemView.text as String) }
        holder.thumbNailItemView.setOnClickListener { mPlayVideo(current.videoId) }
    }

}