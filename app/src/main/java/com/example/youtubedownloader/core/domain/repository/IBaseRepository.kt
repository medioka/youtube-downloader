package com.example.youtubedownloader.core.domain.repository

import androidx.paging.PagingData
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import kotlinx.coroutines.flow.Flow

interface IBaseRepository{
    suspend fun add(video: YtVideo)

    suspend fun remove(video: YtVideo)

    suspend fun update(video: YtVideo)
}

interface IResultRepository : IBaseRepository{
    fun getAllVideoBySearch(searchQuery: String): Flow<PagingData<YtVideo>>

    fun getFavoriteVideo(): Flow<List<YtVideo>>

    fun getResultVideo(): Flow<List<YtVideo>>
}

interface IDownloadRepository : IBaseRepository {
    fun getFinishedVideo(): Flow<List<YtVideo>>
    fun getDownloadingAndPausedVideo(): Flow<List<YtVideo>>
    fun getErrorVideo(): Flow<List<YtVideo>>
    fun getQueueingVideo(): Flow<List<YtVideo>>
}