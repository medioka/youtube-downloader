package com.example.youtubedownloader.format_selector.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.usecase.common.GetVideoInfoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetResultVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_result_video.AddResultVideoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(
    private val getVideoInfoUseCase: GetVideoInfoUseCase,
    private val getResultVideoUseCase: GetResultVideoUseCase,
    private val addResultVideoUseCase: AddResultVideoUseCase
) : ViewModel() {
    private val _isUpdating = MutableStateFlow(false)
    private val _message = MutableStateFlow<Exception?>(null)
    private val _currentVideo = MutableStateFlow<YtVideo?>(null)
    val resultVideos = getResultVideoUseCase().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        listOf()
    )
    private var job: Job? = null

    val isUpdating = _isUpdating.asStateFlow()
    val message = _message.asSharedFlow()
    val currentVideo = _currentVideo.asStateFlow()

    private suspend fun isVideoExist(videoId: String): Boolean {
        val something = getResultVideoUseCase().firstOrNull()
        val condition = something
            ?.find { it.videoId == videoId }
            ?.downloadableFormatList
        val newCondition = condition?.isNotEmpty()
        return newCondition ?: false
    }


    fun addVideo(video: YtVideo) {
        viewModelScope.launch { addResultVideoUseCase(video) }
        _currentVideo.update { video }
    }


    fun getVideoInfo(videoId: String) {
        if (job?.isActive == true) return

        job = viewModelScope.launch {
            updateStatus(true)
            val isVideoExist = async { isVideoExist(videoId) }.await()
            if (isVideoExist) {
                updateStatus(false)
                cancel()
                return@launch
            }
            resultVideos.collectLatest { video ->
                val selectedVideo = video
                    .find { it.videoId == videoId } ?: return@collectLatest
                handleVideoInfoResult(selectedVideo)
            }
        }
    }

    private fun updateStatus(status: Boolean) = _isUpdating.update { status }
    private suspend fun handleVideoInfoResult(video: YtVideo) {
        getVideoInfoUseCase(video).collectLatest { result ->
            when (result) {
                is Resource.Error -> {
                    _message.emit(result.exception)
                    updateStatus(false)
                    job?.cancel()
                }

                is Resource.Success -> {
                    updateStatus(false)
                    job?.cancel()
                }

                is Resource.Loading -> {}
            }
        }

    }

    fun getInitialFormat(videoId: String, type: VideoAudioType): List<DownloadFormat>? {
        val initialFormats =
            resultVideos.value.find { it.videoId == videoId }
                ?.downloadableFormatList?.filter { it.downloadableType == type }
        return initialFormats
    }

    fun getVideoById(videoId: String, type: VideoAudioType): List<DownloadFormat>? {
        val downloadFormat = resultVideos.value
            .find { it.videoId == videoId }
            ?.downloadableFormatList
            ?.filter { it.downloadableType == type }

        return downloadFormat
    }


}