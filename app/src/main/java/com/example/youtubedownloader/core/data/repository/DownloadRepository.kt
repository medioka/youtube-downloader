package com.example.youtubedownloader.core.data.repository

import com.example.youtubedownloader.core.data.local.LocalDataSource
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository
import com.example.youtubedownloader.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DownloadRepository(private val localDataSource: LocalDataSource) :
    IDownloadRepository {
    override suspend fun add(video: YtVideo) {
        val item = DataMapper.mapDomainToDownloadItem(video)
        localDataSource.insertDownloadVideo(item)
    }

    override suspend fun remove(video: YtVideo) {
        val item = DataMapper.mapDomainToDownloadItem(video)
        localDataSource.deleteDownloadVideo(item)
    }

    override suspend fun update(video: YtVideo) {
        val item = DataMapper.mapDomainToDownloadItem(video)
        localDataSource.updateDownloadVideo(item)
    }


    override fun getFinishedVideo(): Flow<List<YtVideo>> {
        return localDataSource.getFinishedVideo().map {
            DataMapper.mapDownloadEntitiesToDomain(it)
        }
    }

    override fun getDownloadingAndPausedVideo(): Flow<List<YtVideo>> {
        return localDataSource.getDownloadingVideo().map {
            DataMapper.mapDownloadEntitiesToDomain(it)
        }
    }

    override fun getErrorVideo(): Flow<List<YtVideo>> {
        return localDataSource.getErrorVideo().map {
            DataMapper.mapDownloadEntitiesToDomain(it)
        }
    }

    override fun getQueueingVideo(): Flow<List<YtVideo>> {
        return localDataSource.getQueueingVideo().map {
            DataMapper.mapDownloadEntitiesToDomain(it)
        }
    }
}