package com.infomantri.bhakti.bhajan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [YoutubeVideoDB::class], version = 1, exportSchema = false)
abstract class YoutubeRoomDatabase : RoomDatabase() {

    abstract fun youtubeVideoDao(): YoutubeVideoDao

    companion object {
        @Volatile
        private var INSTANCE: YoutubeRoomDatabase? = null

        fun getDatabase(context: Context): YoutubeRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YoutubeRoomDatabase::class.java,
                    "youtube_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}