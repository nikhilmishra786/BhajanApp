package com.infomantri.bhakti.bhajan.data.model

import androidx.room.Entity

data class Bhajan(
    var title: String = "",
    var imageUrl: String = "",
    var id: Long = -1,
    var videoId: String = ""
)