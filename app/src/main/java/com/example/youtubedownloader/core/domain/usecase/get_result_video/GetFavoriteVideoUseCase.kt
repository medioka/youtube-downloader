package com.example.youtubedownloader.core.domain.usecase.get_result_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteVideoUseCase(private val repository: IResultRepository) {
    operator fun invoke(): Flow<List<YtVideo>> {
        return repository.getFavoriteVideo()
    }
}