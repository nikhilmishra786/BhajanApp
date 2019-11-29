package com.infomantri.bhakti.bhajan.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_videos_table")
data class YoutubeVideoDB(
    var videoId: String = "",
    var isSent: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)
