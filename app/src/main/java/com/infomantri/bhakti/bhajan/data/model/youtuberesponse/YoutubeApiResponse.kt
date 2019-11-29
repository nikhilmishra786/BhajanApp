package com.infomantri.bhakti.bhajan.data.model.youtuberesponse

data class YoutubeApiResponse(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
)


