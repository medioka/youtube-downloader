package com.example.youtubedownloader.core.domain.usecase.get_download_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository
import kotlinx.coroutines.flow.Flow

class GetQueueingVideoUseCase(private val repository: IDownloadRepository) {
    operator fun invoke(): Flow<List<YtVideo>>{
        return repository.getQueueingVideo()
    }
}