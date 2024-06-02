package com.example.youtubedownloader.core.domain.usecase.modify_download_video

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RemoveDownloadVideoUseCase(private val repository: IDownloadRepository) {
    suspend operator fun invoke(video: YtVideo, isAlsoDeleteFile: Boolean = false) {
        val file = File(video.videoLocation)
        if (video.status == DownloadStatus.DOWNLOADING) {
            YoutubeDL.getInstance().destroyProcessById(video.videoId)
        }
        if (isAlsoDeleteFile && file.exists()) {
            withContext(Dispatchers.IO) {
                file.delete()
            }
        }
        repository.remove(video)
    }
}