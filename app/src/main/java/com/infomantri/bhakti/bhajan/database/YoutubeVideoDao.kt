package com.infomantri.bhakti.bhajan.database

import androidx.room.*

@Dao
interface YoutubeVideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideo(youtubeVideoDB: YoutubeVideoDB)

    @Query("SELECT * from dashboard_videos_table")
    fun getAllVideos(): List<YoutubeVideoDB>

    @Query("SELECT * from dashboard_videos_table Where isSent == :status")
    fun getVideosByStatus(status: Boolean): List<YoutubeVideoDB>

    @Query("SELECT * from dashboard_videos_table WHERE id == :videoId")
    fun getVideoById(videoId: Int): YoutubeVideoDB

    @Update
    fun updateVideo(youtubeVideoDB: YoutubeVideoDB)

    @Delete
    fun deleteVideo(youtubeVideoDB: YoutubeVideoDB)
}