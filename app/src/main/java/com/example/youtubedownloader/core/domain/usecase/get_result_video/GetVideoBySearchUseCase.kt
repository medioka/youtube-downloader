package com.example.youtubedownloader.core.domain.usecase.get_result_video

import androidx.paging.PagingData
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import kotlinx.coroutines.flow.Flow

class  GetVideoBySearchUseCase(private val repository: IResultRepository) {
    operator fun invoke(searchQuery: String): Flow<PagingData<YtVideo>> {
        return repository.getAllVideoBySearch(searchQuery)
    }
}