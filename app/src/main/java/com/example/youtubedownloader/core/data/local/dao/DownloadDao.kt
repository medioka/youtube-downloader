package com.example.youtubedownloader.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.youtubedownloader.core.data.local.entities.DownloadItem
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: DownloadItem)

    @Delete
    suspend fun delete(item: DownloadItem)

    @Update
    suspend fun update(item: DownloadItem)

    //GET VIDEOS BASED ON ITS DOWNLOAD STATUS
    @Query("SELECT * FROM download_item WHERE status =:status")
    fun getQueueingVideo(status: DownloadStatus = DownloadStatus.QUEUEING): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_item WHERE status =:status OR status =:status2")
    fun getDownloadingVideo(
        status: DownloadStatus = DownloadStatus.DOWNLOADING,
        status2: DownloadStatus = DownloadStatus.PAUSE
    ): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_item WHERE status =:status")
    fun getFinishedVideo(status: DownloadStatus = DownloadStatus.FINISHED): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_item WHERE status =:status")
    fun getErrorVideo(status: DownloadStatus = DownloadStatus.ERROR): Flow<List<DownloadItem>>
}