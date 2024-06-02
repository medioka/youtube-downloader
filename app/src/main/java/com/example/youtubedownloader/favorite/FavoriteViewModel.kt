package com.example.youtubedownloader.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.core.domain.usecase.modify_result_video.DeleteVideoFromFavoriteUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetFavoriteVideoUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val deleteUseCase: DeleteVideoFromFavoriteUseCase,
    private val favoriteVideoUseCase: GetFavoriteVideoUseCase
) : ViewModel() {
    val videos = favoriteVideoUseCase().distinctUntilChanged().map {
        Resource.Success(it)
    }.catch {
        Resource.Error<YtVideo>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Resource.Loading
    )

    fun removeVideoFromFavorite(video: YtVideo) {
        viewModelScope.launch {
            deleteUseCase(video)
        }
    }
}