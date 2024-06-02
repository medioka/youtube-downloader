package com.example.youtubedownloader.core.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.data.remote.ApiResponse
import com.example.youtubedownloader.core.data.remote.RemoteDataSource
import com.example.youtubedownloader.core.utils.DataMapper
import kotlinx.coroutines.flow.first

class ResultPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val query: String
) : PagingSource<String, YtVideo>() {
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, YtVideo> {
        val token: String? = params.key

        try {
            val response = remoteDataSource.getAllVideo(query, token).first()
            when (response) {
                is ApiResponse.Success -> {
                    val resultData = DataMapper.mapRemoteToDomain(response.data.videos)
                    return LoadResult.Page(
                        data = resultData,
                        prevKey = null, // Only paging forward.
                        nextKey = response.data.nextPageToken
                    )
                }


                is ApiResponse.Empty -> {
                    Log.d("Paging Source", "Empty")
                    return LoadResult.Page(
                        data = listOf(),
                        prevKey = null, // Only paging forward.
                        nextKey = null
                    )
                }

                is ApiResponse.Error -> {
                    Log.e("Paging Source", "Error default : ${response.errorMessage.toString()}")
                    return LoadResult.Error(Exception(response.errorMessage))
                }

            }
        } catch (e: Exception) {
            Log.e("Paging Source", "Error Exception : ${e.toString()}")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, YtVideo>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}
