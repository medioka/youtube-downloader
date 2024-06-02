package com.example.youtubedownloader.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetAllDownloadStatusVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.ModifyDownloadVideoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadViewModel(
    getAllDownloadStatusVideoUseCase: GetAllDownloadStatusVideoUseCase,
    private val modifyDownloadVideoUseCase: ModifyDownloadVideoUseCase
) : ViewModel() {
    private val _selectedVideo = MutableStateFlow<YtVideo?>(null)
    val selectedVideo = _selectedVideo.asStateFlow()

    val queueingVideo = getAllDownloadStatusVideoUseCase.getQueueingVideoUseCase().map {
        Resource.Success(it)
    }.catch {
        Resource.Error<List<YtVideo>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    val finishedVideo = getAllDownloadStatusVideoUseCase.getFinishedVideoUseCase().map {
        Resource.Success(it)
    }.catch { Resource.Error<List<YtVideo>>(Exception(it)) }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    val downloadingVideo = getAllDownloadStatusVideoUseCase.getDownloadingVideoUseCase().map {
        Resource.Success(it)
    }.catch {
        Resource.Error<List<YtVideo>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    val errorVideo = getAllDownloadStatusVideoUseCase.getErrorVideoUseCase().map {
        Resource.Success(it)
    }.catch {
        Resource.Error<List<YtVideo>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    fun addToQueue(video: YtVideo, isFileExist: Boolean = false) {
        viewModelScope.launch {
            modifyDownloadVideoUseCase.addToDownloadVideoUseCase(video, isFileExist)
        }
    }

    fun removeFromDownload(isAlsoDeleteFile: Boolean = false) {
        viewModelScope.launch {
            _selectedVideo.value?.let {
                modifyDownloadVideoUseCase.removeDownloadVideoUseCase(
                    it,
                    isAlsoDeleteFile
                )
            }
            _selectedVideo.update { null }
        }
    }

    fun selectVideo(video: YtVideo?) {
        _selectedVideo.update { video }
    }


}