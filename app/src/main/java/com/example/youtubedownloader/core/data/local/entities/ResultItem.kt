package com.example.youtubedownloader.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youtubedownloader.core.domain.model.DownloadFormat

@Entity("result_item")
data class ResultItem(
    @PrimaryKey(autoGenerate = false)
    val videoId: String,
    val artist: String,
    val title: String,
    val publishedDate: String,
    var isFavorite: Boolean = false,
    val thumbnail: String,
    var downloadableFormatList: List<DownloadFormat> = listOf()
)