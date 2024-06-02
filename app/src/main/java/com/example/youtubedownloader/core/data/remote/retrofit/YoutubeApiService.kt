package com.example.youtubedownloader.core.data.remote.retrofit

import com.example.youtubedownloader.BuildConfig
import com.example.youtubedownloader.core.data.ResultPagingSource
import com.example.youtubedownloader.core.data.remote.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {
    @GET("search")
    suspend fun getAllVideo(
        @Query("q") searchQuery: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = ResultPagingSource.PAGE_SIZE,
        @Query("key") key: String = BuildConfig.YOUTUBE_API
    ): SearchResponse


    companion object {
        const val URL = "https://youtube.googleapis.com/youtube/v3/"
    }
}