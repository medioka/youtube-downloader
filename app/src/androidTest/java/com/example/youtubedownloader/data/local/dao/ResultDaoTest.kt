package com.example.youtubedownloader.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.youtubedownloader.core.data.local.dao.ResultDao
import com.example.youtubedownloader.core.data.local.database.YtDatabase
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.utils.DataMapper
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResultDaoTest {
    private lateinit var database: YtDatabase
    private lateinit var resultDao: ResultDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            YtDatabase::class.java
        ).allowMainThreadQueries().build()
        resultDao = database.resultDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun when_getVideo_expect_sizeThree() = runTest {
        setupInitialData()

        val initialVideos = resultDao.getFavoriteVideo().first()
        assertThat(initialVideos).hasSize(3)
    }

    @Test
    fun when_removeLastVideo_expect_sizeTwo() = runTest {
        setupInitialData()
        val initialVideos = resultDao.getFavoriteVideo().first()

        resultDao.delete(initialVideos.last())
        val modifiedVideos = resultDao.getFavoriteVideo().first()
        assertThat(modifiedVideos).hasSize(2)
    }

    @Test
    fun when_updateLastVideo_expect_updated() = runTest {
        setupInitialData()
        val initialVideos = resultDao.getFavoriteVideo().first()
        val lastVideo = initialVideos.last().copy(title = "Updated title")
        resultDao.update(lastVideo)
        val modifiedVideos = resultDao.getFavoriteVideo().first()
        assertThat(modifiedVideos).contains(lastVideo)
    }

    @Test
    fun when_searchById_expect_exist() = runTest {
        setupInitialData()
        val video = resultDao.getResultVideo().first()
        assertThat(video).isNotNull()
    }

    private suspend fun setupInitialData() {
        repeat(3) { index ->
            val video = YtVideo(
                videoId = "Id: $index",
                title = "title: $index",
                artist = "artist: $index",
                publishedDate = "publish: $index",
                thumbnail = "thumbnail $index",
                isFavorite = true
            )
            val resultItem = DataMapper.mapDomainToResultItem(video)
            resultDao.insert(resultItem)
        }
    }

}