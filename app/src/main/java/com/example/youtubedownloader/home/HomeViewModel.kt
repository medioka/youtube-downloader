package com.example.youtubedownloader.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.usecase.modify_result_video.AddResultVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetVideoBySearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideoBySearchUseCase: GetVideoBySearchUseCase,
    private val addResultVideoUseCase: AddResultVideoUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val paginationFlow = _searchQuery.filter { it.isNotEmpty() }.distinctUntilChanged().map {
        getVideoBySearchUseCase(it).cachedIn(viewModelScope).first()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        PagingData.empty()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun addVideoToFavorite(video: YtVideo) {
        viewModelScope.launch {
            addResultVideoUseCase(
                video = video,
                isFavorite = true
            )
        }
    }
}