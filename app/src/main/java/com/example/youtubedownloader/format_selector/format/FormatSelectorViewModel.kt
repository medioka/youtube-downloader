package com.example.youtubedownloader.format_selector.format

import androidx.lifecycle.ViewModel
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormatSelectorViewModel : ViewModel() {
    private val _currentItem = MutableStateFlow<DownloadFormat?>(null)
    private val _newList = MutableStateFlow<List<DownloadFormat>>(listOf())

    val currentItem = _currentItem.asStateFlow()
    val newList = _newList.asStateFlow()

    fun updateCurrentItem(itemFormat: DownloadFormat?) {
        _currentItem.update { itemFormat }
    }

    fun updateList(items: List<DownloadFormat>) {
        _newList.update { items }
    }
}