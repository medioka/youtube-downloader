package com.example.youtubedownloader.core.data.local

import com.example.youtubedownloader.core.data.local.dao.DownloadDao
import com.example.youtubedownloader.core.data.local.dao.ResultDao
import com.example.youtubedownloader.core.data.local.entities.DownloadItem
import com.example.youtubedownloader.core.data.local.entities.ResultItem

class LocalDataSource(private val downloadDao: DownloadDao, private val resultDao: ResultDao) {
    //DOWNLOAD DAO
    suspend fun insertDownloadVideo(item: DownloadItem) = downloadDao.insert(item)
    suspend fun deleteDownloadVideo(item: DownloadItem) = downloadDao.delete(item)
    suspend fun updateDownloadVideo(item: DownloadItem) = downloadDao.update(item)

    fun getQueueingVideo() = downloadDao.getQueueingVideo()
    fun getDownloadingVideo() = downloadDao.getDownloadingVideo()
    fun getFinishedVideo() = downloadDao.getFinishedVideo()
    fun getErrorVideo() = downloadDao.getErrorVideo()


    //RESULT DAO
    suspend fun insertVideo(item: ResultItem) = resultDao.insert(item)
    suspend fun deleteVideo(item: ResultItem) = resultDao.insert(item)
    suspend fun updateVideo(item: ResultItem) = resultDao.update(item)
    fun getResultVideo() = resultDao.getResultVideo()
    fun getFavoriteVideo() = resultDao.getFavoriteVideo()


}