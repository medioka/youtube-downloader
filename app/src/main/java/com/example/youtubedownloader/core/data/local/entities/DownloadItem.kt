package com.example.youtubedownloader.core.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.model.DownloadOption


@Entity("download_item")
data class DownloadItem(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val artist: String,
    val title: String,
    val publishedDate: String,
    val thumbnail: String,
    var status: DownloadStatus,
    var videoLocation: String,
    @Embedded
    val downloadFormat: DownloadFormat,
    @Embedded
    val downloadOption: DownloadOption
)





