package com.example.youtubedownloader.core.utils

import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.AudioContainer
import com.example.youtubedownloader.core.domain.model.Container
import com.example.youtubedownloader.core.domain.model.DefaultContainer
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.model.VideoContainer

object DefaultValueUtils {

    fun getDefaultValueByType(type: VideoAudioType) : List<DownloadFormat> {
        return if (type == VideoAudioType.VIDEO || type == VideoAudioType.DEFAULT_AUDIO) {
            defaultVideoList()
        } else {
            defaultAudioList()
        }
    }

    fun defaultVideoList(): List<DownloadFormat> {
        return listOf(
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "2160", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "1440", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "1080", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "720", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "480", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "360", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "240", "~", "~", "DEFAULT"),
            DownloadFormat(VideoAudioType.DEFAULT_VIDEO, "DEFAULT", "144", "~", "~", "DEFAULT"),
        )
    }

    fun defaultAudioList(): List<DownloadFormat> {
        return listOf(
            DownloadFormat(
                VideoAudioType.DEFAULT_AUDIO,
                "DEFAULT",
                "BEST AUDIO",
                "ba",
                "~",
                "DEFAULT"
            ),
            DownloadFormat(
                VideoAudioType.DEFAULT_AUDIO,
                "DEFAULT",
                "WORST AUDIO",
                "wa",
                "~",
                "DEFAULT"
            )
        )
    }

    fun getAudioContainers(): List<String> {
        return listOf(
            Container.Default(DefaultContainer.DEFAULT).container.name,
            Container.Audio(AudioContainer.MP3).container.name,
            Container.Audio(AudioContainer.AAC).container.name,
            Container.Audio(AudioContainer.M4A).container.name
        )
    }

    fun getVideoContainer(): List<String> {
        return listOf(
            Container.Default(DefaultContainer.DEFAULT).container.name,
            Container.Video(VideoContainer.MKV).container.name,
            Container.Video(VideoContainer.MP4).container.name,
            Container.Video(VideoContainer.WEBM).container.name
        )
    }


}