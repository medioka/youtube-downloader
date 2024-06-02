package com.example.youtubedownloader.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youtubedownloader.core.data.local.converter.DownloadableFormatListConverter
import com.example.youtubedownloader.core.data.local.dao.DownloadDao
import com.example.youtubedownloader.core.data.local.dao.ResultDao
import com.example.youtubedownloader.core.data.local.entities.DownloadItem
import com.example.youtubedownloader.core.data.local.entities.ResultItem

@Database(entities = [ResultItem::class, DownloadItem::class], version = 1, exportSchema = false)
@TypeConverters(DownloadableFormatListConverter::class)
abstract class YtDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun resultDao(): ResultDao

    companion object {
        @Volatile
        private var INSTANCE: YtDatabase? = null

        fun getInstance(context: Context): YtDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YtDatabase::class.java,
                    "ytdl.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
    }

}