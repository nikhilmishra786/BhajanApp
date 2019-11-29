package com.infomantri.bhakti.bhajan.database

import androidx.annotation.WorkerThread

class YoutubeVideoRepository(
    private val youtubeVideoDao: YoutubeVideoDao,
    videoStatus: Boolean = false
) {

    val allYoutubeVideoDBS: List<YoutubeVideoDB> = youtubeVideoDao.getAllVideos()

    val videosByStatuses: List<YoutubeVideoDB> = youtubeVideoDao.getVideosByStatus(videoStatus)

    @WorkerThread
    fun insertVideo(youtubeVideoDB: YoutubeVideoDB) {
        youtubeVideoDao.insertVideo(youtubeVideoDB)
    }

    @WorkerThread
    fun updateVideo(youtubeVideoDB: YoutubeVideoDB) {
        youtubeVideoDao.updateVideo(youtubeVideoDB)
    }

    @WorkerThread
    fun deleteVideo(youtubeVideoDB: YoutubeVideoDB) {
        youtubeVideoDao.deleteVideo(youtubeVideoDB)
    }
}