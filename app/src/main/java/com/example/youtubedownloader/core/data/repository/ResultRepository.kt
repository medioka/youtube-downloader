package com.example.youtubedownloader.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.youtubedownloader.core.data.ResultPagingSource
import com.example.youtubedownloader.core.data.local.LocalDataSource
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.data.remote.RemoteDataSource
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import com.example.youtubedownloader.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : IResultRepository {
    override suspend fun add(video: YtVideo) {
        val item = DataMapper.mapDomainToResultItem(video)
        localDataSource.insertVideo(item)
    }

    override suspend fun remove(video: YtVideo) {
        val item = DataMapper.mapDomainToResultItem(video)
        localDataSource.deleteVideo(item)
    }

    override suspend fun update(video: YtVideo) {
        val item = DataMapper.mapDomainToResultItem(video)
        localDataSource.updateVideo(item)
    }

    override fun getAllVideoBySearch(searchQuery: String): Flow<PagingData<YtVideo>> {
        return Pager(
            config = PagingConfig(
                pageSize = ResultPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = {
                ResultPagingSource(remoteDataSource, searchQuery)
            }
        ).flow
    }

    override fun getResultVideo(): Flow<List<YtVideo>> {
        return localDataSource.getResultVideo().map {
            DataMapper.mapResultEntitiesToDomain(it)
        }
    }

    override fun getFavoriteVideo(): Flow<List<YtVideo>> {
        return localDataSource.getFavoriteVideo().map {
            DataMapper.mapResultEntitiesToDomain(it)
        }
    }
}