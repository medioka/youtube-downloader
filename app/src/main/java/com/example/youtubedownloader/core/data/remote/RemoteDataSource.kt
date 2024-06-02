package com.example.youtubedownloader.core.data.remote

import android.util.Log
import com.example.youtubedownloader.core.data.remote.response.search.Item
import com.example.youtubedownloader.core.data.remote.retrofit.YoutubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface RemoteDataSource {
    suspend fun getAllVideo(
        searchQuery: String,
        token: String? = null
    ): Flow<ApiResponse<VideoListAndToken>>
}

class RemoteDataSourceImpl(private val apiService: YoutubeApiService) : RemoteDataSource {
    override suspend fun getAllVideo(
        searchQuery: String,
        token: String?
    ): Flow<ApiResponse<VideoListAndToken>> {
        return flow {
            try {
                val response = apiService.getAllVideo(searchQuery, token)
                val data = response.items
                if (!data.isNullOrEmpty()) {
                    val filteredData = data.filterNotNull()
                    val videoListAndToken = VideoListAndToken(filteredData, response.nextPageToken)
                    Log.d("Api Response", "Success: ${filteredData.size}")
                    emit(ApiResponse.Success(videoListAndToken))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}

data class VideoListAndToken(
    val videos: List<Item>,
    val nextPageToken: String?
)