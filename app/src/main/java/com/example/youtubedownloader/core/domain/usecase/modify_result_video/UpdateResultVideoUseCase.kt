package com.example.youtubedownloader.core.domain.usecase.modify_result_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IResultRepository

class UpdateResultVideoUseCase(private val repository: IResultRepository) {
    suspend operator fun invoke(video: YtVideo) {
        repository.update(video)
    }
}