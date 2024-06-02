package com.example.youtubedownloader.format_selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.model.DownloadOption
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.ModifyDownloadVideoUseCase
import com.example.youtubedownloader.core.utils.DefaultValueUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadConfigViewModel(
    private val modifyDownloadVideoUseCase: ModifyDownloadVideoUseCase
) : ViewModel() {

    private val _currentVideo = MutableStateFlow<YtVideo?>(null)
    private val _currentAudioFormat = MutableStateFlow<DownloadFormat?>(null)
    private val _currentVideoFormat = MutableStateFlow<DownloadFormat?>(null)
    private val _videoContainerIndex = MutableStateFlow(0)
    private val _audioContainerIndex = MutableStateFlow(0)
    private val _audioPath = MutableStateFlow("")
    private val _videoPath = MutableStateFlow("")

    val currentVideo = _currentVideo.asStateFlow()
    val currentAudioFormat = _currentAudioFormat.asStateFlow()
    val currentVideoFormat = _currentVideoFormat.asStateFlow()
    val videoContainerIndex = _videoContainerIndex.asStateFlow()
    val audioContainerIndex = _audioContainerIndex.asStateFlow()
    val audioPath = _audioPath.asStateFlow()
    val videoPath = _videoPath.asStateFlow()

    private fun configureDownload(type: VideoAudioType, video: YtVideo): YtVideo {
        val videoContainer = DefaultValueUtils.getVideoContainer()[videoContainerIndex.value]
        val audioContainer = DefaultValueUtils.getAudioContainers()[audioContainerIndex.value]
        when (type) {
            VideoAudioType.VIDEO -> {
                val videoLocation = videoPath.value
                val downloadFormat = currentVideoFormat.value
                val downloadOption = DownloadOption(container = videoContainer)
                return video.copy(
                    videoLocation = videoLocation,
                    downloadFormat = downloadFormat,
                    downloadOption = downloadOption
                )
            }

            VideoAudioType.AUDIO -> {
                val videoLocation = audioPath.value
                val downloadFormat = currentAudioFormat.value
                val downloadOption = DownloadOption(container = audioContainer)
                return video.copy(
                    videoLocation = videoLocation,
                    downloadFormat = downloadFormat,
                    downloadOption = downloadOption
                )
            }

            else -> throw IllegalArgumentException("No such type")
        }
    }

    fun addToQueue(video: YtVideo, type: VideoAudioType, isFileExist: Boolean = false) {
        val updatedVideo = configureDownload(type, video)
        viewModelScope.launch {
            modifyDownloadVideoUseCase.addToDownloadVideoUseCase(updatedVideo, isFileExist)
        }
    }

    fun updatePathBasedOnType(type: VideoAudioType, path: String) {
        if (type == VideoAudioType.VIDEO || type == VideoAudioType.DEFAULT_VIDEO) {
            _videoPath.update { path }
        } else {
            _audioPath.update { path }
        }
    }

    fun updateCurrentVideo(video: YtVideo) {
        _currentVideo.update { video }
    }

    fun updateDownloadFormat(downloadFormat: DownloadFormat) {
        val type = downloadFormat.downloadableType
        if (type == VideoAudioType.VIDEO || type == VideoAudioType.DEFAULT_VIDEO) {
            _currentVideoFormat.update { downloadFormat }
        } else {
            _currentAudioFormat.update { downloadFormat }
        }
    }

    fun updateCurrentContainer(position: Int, type: VideoAudioType) {
        if (type == VideoAudioType.VIDEO || type == VideoAudioType.DEFAULT_VIDEO) {
            _videoContainerIndex.update { position }
        } else {
            _audioContainerIndex.update { position }
        }
    }


}