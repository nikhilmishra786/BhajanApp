package com.infomantri.bhakti.bhajan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "youtube_videos_table")
data class YoutubeVideo(
    val title: String,
    var videoId: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)