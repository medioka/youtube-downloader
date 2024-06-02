package com.example.youtubedownloader.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.youtubedownloader.core.data.local.dao.DownloadDao
import com.example.youtubedownloader.core.data.local.database.YtDatabase
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.utils.DataMapper
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DownloadDaoTest {
    private lateinit var database: YtDatabase
    private lateinit var downloadDao: DownloadDao
    private val downloadFormat = DownloadFormat(
        VideoAudioType.VIDEO,
        "",
        "",
        "",
        "",
        ""
    )

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            YtDatabase::class.java
        ).allowMainThreadQueries().build()
        downloadDao = database.downloadDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun when_getQueueingVideo_expect_sizeOfOne() = runTest {
        setupInitialData()
        val initialVideo = downloadDao.getQueueingVideo().first()
        assertThat(initialVideo).hasSize(1)
    }

    @Test
    fun when_getDownloadingVideo_expect_sizeOfOne() = runTest {
        setupInitialData()
        val initialVideo = downloadDao.getDownloadingVideo().first()
        assertThat(initialVideo).hasSize(1)
    }

    @Test
    fun when_getErrorVideo_expect_sizeOfOne() = runTest {
        setupInitialData()
        val initialVideo = downloadDao.getErrorVideo().first()
        assertThat(initialVideo).hasSize(1)
    }

    @Test
    fun when_getFinishedVideo_expect_sizeOfOne() = runTest {
        setupInitialData()
        val initialVideo = downloadDao.getFinishedVideo().first()
        assertThat(initialVideo).hasSize(1)
    }

    @Test
    fun when_removeLastVideo_expect_sizeTwo() = runTest {
        setupInitialData()
        val queueingVideo = YtVideo(
            videoId = "Id: 0",
            title = "title: 0",
            artist = "artist: 0",
            publishedDate = "publish: 0",
            downloadFormat = downloadFormat,
            thumbnail = "thumbnail 0",
            isFavorite = true,
            status = DownloadStatus.QUEUEING
        )
        downloadDao.delete(DataMapper.mapDomainToDownloadItem(queueingVideo))
        val modifiedVideos = downloadDao.getQueueingVideo().first()
        assertThat(modifiedVideos).hasSize(0)
    }

    @Test
    fun when_updateLastVideo_expect_updated() = runTest {
        setupInitialData()
        val video = YtVideo(
            videoId = "Id: 0",
            title = "title: 0",
            artist = "artist: 0",
            publishedDate = "publish: 0",
            downloadFormat = downloadFormat,
            thumbnail = "thumbnail 0",
            isFavorite = true,
            status = DownloadStatus.QUEUEING
        )
        val initialVideos = downloadDao.getQueueingVideo().first()
        assertThat(initialVideos.first().title).matches("title: 0")
        assertThat(initialVideos).hasSize(1)

        val videoToBeUpdated = DataMapper.mapDomainToDownloadItem(video.copy(title = "New title"))
        downloadDao.update(videoToBeUpdated)

        val modifiedVideos = downloadDao.getQueueingVideo().first()
        assertThat(modifiedVideos).contains(videoToBeUpdated)
        assertThat(modifiedVideos).hasSize(1)
    }

    private suspend fun setupInitialData() {
        val downloadStatus = listOf(
            DownloadStatus.QUEUEING,
            DownloadStatus.DOWNLOADING,
            DownloadStatus.ERROR,
            DownloadStatus.FINISHED
        )
        repeat(4) { index ->
            val video = YtVideo(
                videoId = "Id: $index",
                title = "title: $index",
                artist = "artist: $index",
                publishedDate = "publish: $index",
                thumbnail = "thumbnail $index",
                isFavorite = true,
                downloadFormat = downloadFormat,
                status = downloadStatus[index]
            )
            val downloadItem = DataMapper.mapDomainToDownloadItem(video)
            downloadDao.insert(downloadItem)
        }
    }
}