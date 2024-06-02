package com.example.youtubedownloader.core.domain.usecase.modify_result_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetResultVideoUseCase
import kotlinx.coroutines.flow.firstOrNull

class AddResultVideoUseCase(
    private val repository: IResultRepository,
    private val getResultVideoUseCase: GetResultVideoUseCase,
    private val updateResultVideoUseCase: UpdateResultVideoUseCase
) {
    suspend operator fun invoke(video: YtVideo, isFavorite: Boolean = false) {
        repository.add(video)

        val value = getResultVideoUseCase()
            .firstOrNull()
            ?.find { it.videoId == video.videoId }
        if (value != null) {
            val updatedFavorite = value.isFavorite || isFavorite
            updateResultVideoUseCase(value.copy(isFavorite = updatedFavorite))
        } else {
            updateResultVideoUseCase(video.copy(isFavorite = isFavorite))
        }


    }
}