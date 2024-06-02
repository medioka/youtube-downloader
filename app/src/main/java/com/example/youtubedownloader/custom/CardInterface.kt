package com.example.youtubedownloader.custom

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.model.DownloadFormat


interface CardClickListener {
    fun onClickCard(video: YtVideo)
}

interface CardLongClickListener {
    fun onLongClickCard(video: YtVideo)
}

interface BaseCardClickListener : CardClickListener, CardLongClickListener

interface FavoriteClickListener {
    fun onClickFavorite(video: YtVideo)
}

interface DownloadClickListener {
    fun onClickDownload(video: YtVideo)
}

interface ResultVideoListener : CardClickListener, FavoriteClickListener, DownloadClickListener

interface FormatClickListener {
    fun onFormatClickListener(downloadFormat: DownloadFormat)
}

interface DeleteClickListener {
    fun onDeleteClick(video: YtVideo)
}

interface ShareClickListener {
    fun onShareClick(id: String)
}

interface ClickAndShareListener : ShareClickListener, CardClickListener

interface VideoUpdaterListener {
    fun updateVideoPercentage(
        percentageUpdater: (Float) -> Unit,
        descriptionUpdater: (String) -> Unit
    )
}

interface DownloadingVideoListener : VideoUpdaterListener, CardLongClickListener

interface DownloadedVideoListener : ShareClickListener, DeleteClickListener, CardClickListener
