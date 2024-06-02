package com.example.youtubedownloader.core.domain.usecase.modify_download_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository

class UpdateDownloadVideoUseCase(private val repository: IDownloadRepository) {
    suspend operator fun invoke(video: YtVideo) {
        repository.update(video)
    }
}