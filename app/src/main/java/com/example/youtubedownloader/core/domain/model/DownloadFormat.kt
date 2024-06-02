package com.example.youtubedownloader.core.domain.model

import android.os.Parcelable
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadFormat(
    val downloadableType: VideoAudioType,
    val formatId: String,
    val formatNote: String,
    val decoder: String,
    val fileSize: String,
    val externalType: String
): Parcelable