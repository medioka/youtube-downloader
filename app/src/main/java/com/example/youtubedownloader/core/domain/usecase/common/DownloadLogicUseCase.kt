package com.example.youtubedownloader.core.domain.usecase.common

import android.app.Application
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.ModifyDownloadVideoUseCase
import com.example.youtubedownloader.core.utils.FileUtils
import com.example.youtubedownloader.core.utils.RequestUtils
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class DownloadLogicUseCase(
    private val modifyUseCase: ModifyDownloadVideoUseCase,
    private val application: Application
) {
    val isDownloading = MutableStateFlow(false)


    suspend fun startDownloading(video: YtVideo, callback: (Float, String) -> Unit) {
        video.status = DownloadStatus.DOWNLOADING
        callback(0F, "")
        isDownloading.update { true }
        modifyUseCase.updateDownloadVideoUseCase(video)

        val callbacks = { downloadProgress: Float, _: Long, line: String ->
            callback(downloadProgress, line)
        }

        val resultVideo = runDownloadProgress(video, callbacks, callback)

        //UPDATE ITEM WHEN DONE
        val updatedVideo = resultVideo?.copy(status = DownloadStatus.FINISHED)
            ?: video.copy(status = DownloadStatus.FINISHED)
        modifyUseCase.updateDownloadVideoUseCase(updatedVideo)
    }

    private suspend fun runDownloadProgress(
        video: YtVideo,
        downloadCallback: (Float, Long, String) -> Unit,
        videoCallback: (Float, String) -> Unit

    ): YtVideo? {
        if (video.downloadFormat == null) throw IllegalArgumentException("Video formats are null")
        stopDownload(video, videoCallback)
        return withContext(Dispatchers.IO) {
            val request = RequestUtils.createDownloadRequest(video, application)
            YoutubeDL.getInstance().execute(request, video.videoId, downloadCallback).let {
                val updatedVideo = FileUtils.moveFileFromTempToDestination(
                    video,
                    application.applicationContext,
                    it.out
                )
                updatedVideo
            }
        }
    }

    fun stopDownload(video: YtVideo, callback: (Float, String) -> Unit) {
        isDownloading.update { false }
        YoutubeDL.getInstance().destroyProcessById(video.videoId)
        callback(0F, "")
    }
}