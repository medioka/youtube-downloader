package com.example.youtubedownloader.core.data.local.entities

import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.model.DownloadOption

data class YtVideo(
    val videoId: String,
    var title: String,
    val artist: String,
    val publishedDate: String,
    var thumbnail: String,
    var isFavorite: Boolean = false,
    var downloadFormat: DownloadFormat? = null,
    var progress: Float = 0F,
    var downloadableFormatList: List<DownloadFormat> = listOf(),
    var status: DownloadStatus = DownloadStatus.NOT_DOWNLOADING,
    val downloadOption: DownloadOption = DownloadOption(),
    var videoLocation: String = ""
)