package com.example.youtubedownloader

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.usecase.common.DownloadLogicUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetAllDownloadStatusVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.ModifyDownloadVideoUseCase
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadProcess(
    getAllDownloadVideo: GetAllDownloadStatusVideoUseCase,
    private val downloadLogicUseCase: DownloadLogicUseCase,
    private val modifyDownloadVideoUseCase: ModifyDownloadVideoUseCase
) {

    private val _progress = MutableStateFlow(0F)
    private val _downloadCount = MutableSharedFlow<Int>()
    private val _progressDescription = MutableStateFlow("")

    val progress = _progress.asStateFlow()
    val downloadCount = _downloadCount.asSharedFlow()
    val progressDescription = _progressDescription.asStateFlow()

    private val queueingVideo = getAllDownloadVideo.getQueueingVideoUseCase()
    private val downloadVideo = getAllDownloadVideo.getDownloadingVideoUseCase()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val videoList = downloadVideo.firstOrNull()
            videoList?.let { downloadUnfinishedVideo(it) }
            downloadVideoListener()
        }
    }

    private suspend fun downloadUnfinishedVideo(unfinishedVideo: List<YtVideo>) {
        for (video in unfinishedVideo) {
            startDownloading(video)
        }
    }

    private suspend fun downloadVideoListener() {
        combine(
            downloadLogicUseCase.isDownloading,
            queueingVideo,
            downloadVideo
        ) { isDownloading, queueingVideos, downloadingVideos ->
            val isQueueEmpty = queueingVideos.isEmpty()
            val isDownloadingEmpty = downloadingVideos.isEmpty()
            !isDownloading && (isQueueEmpty || isDownloadingEmpty)
        }.collect { isAllowedToDownload ->
            println("Allowed to download: $isAllowedToDownload")
            if (!isAllowedToDownload) return@collect
            val video = queueingVideo.firstOrNull()?.firstOrNull() ?: return@collect
            startDownloading(video)
        }
    }

    private suspend fun startDownloading(video: YtVideo) {
        val callback = { percentage: Float, line: String ->
            _progress.update { percentage }
            _progressDescription.update { line }
        }
        try {
            downloadLogicUseCase.startDownloading(video, callback)
            _downloadCount.emit(1)
        } catch (e: Exception) {
            errorHandler(e, video)
            println("Error: $e")
        } finally {
            downloadLogicUseCase.stopDownload(video, callback)
        }
    }

    private suspend fun errorHandler(e: Exception, video: YtVideo) {
        if (e is YoutubeDL.CanceledException) return
        modifyDownloadVideoUseCase.updateDownloadVideoUseCase(video.copy(status = DownloadStatus.ERROR))
    }
}