package com.example.youtubedownloader.core.utils

import com.example.youtubedownloader.core.data.local.entities.DownloadItem
import com.example.youtubedownloader.core.data.local.entities.ResultItem
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.data.remote.response.search.Item
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.yausername.youtubedl_android.mapper.VideoFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object DataMapper {
    private fun isContentNullOn(item: Item): Boolean {
        if (item.snippet == null) return true
        if (item.id?.videoId == null) return true
        val snippet = item.snippet
        if (snippet.title == null) return true
        if (snippet.channelTitle == null) return true
        if (snippet.publishedAt == null) return true
        if (snippet.thumbnails?.high?.url == null) return true
        return false
    }

    private suspend fun createYtVideoFrom(item: Item): YtVideo {
        return withContext(Dispatchers.Default) {
            val snippet = item.snippet!!
            val video = YtVideo(
                videoId = item.id!!.videoId!!,
                artist = snippet.channelTitle!!,
                title = DataFormatter.titleFormatter(snippet.title!!),
                publishedDate = DataFormatter.convertToStandardDateTime(snippet.publishedAt!!),
                thumbnail = snippet.thumbnails!!.high!!.url!!
            )
            video
        }
    }


    suspend fun mapRemoteToDomain(items: List<Item>): List<YtVideo> {
        val finalVideo = arrayListOf<YtVideo>()
        for (item in items) {
            if (isContentNullOn(item)) continue
            finalVideo.add(createYtVideoFrom(item))
        }
        return finalVideo
    }

    suspend fun mapResultEntitiesToDomain(items: List<ResultItem>): List<YtVideo> {
        return withContext(Dispatchers.Default) {
            items.map {
                async {
                    YtVideo(
                        videoId = it.videoId,
                        title = it.title,
                        artist = it.artist,
                        publishedDate = it.publishedDate,
                        thumbnail = it.thumbnail,
                        downloadableFormatList = it.downloadableFormatList,
                        isFavorite = it.isFavorite
                    )
                }
            }.awaitAll()
        }
    }

    fun mapDomainToResultItem(item: YtVideo): ResultItem {
        return ResultItem(
            videoId = item.videoId,
            artist = item.artist,
            title = item.title,
            publishedDate = item.publishedDate,
            thumbnail = item.thumbnail,
            downloadableFormatList = item.downloadableFormatList,
            isFavorite = item.isFavorite
        )
    }

    fun mapResultItemToDomain(item: ResultItem?): YtVideo? {
        if (item == null) return null
        return YtVideo(
            videoId = item.videoId,
            artist = item.artist,
            title = item.title,
            publishedDate = item.publishedDate,
            thumbnail = item.thumbnail,
            downloadableFormatList = item.downloadableFormatList,
            isFavorite = item.isFavorite
        )
    }

    fun mapDomainToDownloadItem(item: YtVideo): DownloadItem {
        return DownloadItem(
            id = item.videoId,
            title = item.title,
            artist = item.artist,
            publishedDate = item.publishedDate,
            thumbnail = item.thumbnail,
            downloadFormat = item.downloadFormat!!,
            status = item.status,
            videoLocation = item.videoLocation,
            downloadOption = item.downloadOption
        )
    }

    suspend fun mapDownloadEntitiesToDomain(items: List<DownloadItem>): List<YtVideo> {
        return withContext(Dispatchers.Default) {
            items.map {
                async {
                    YtVideo(
                        videoId = it.id,
                        title = it.title,
                        artist = it.artist,
                        publishedDate = it.publishedDate,
                        thumbnail = it.thumbnail,
                        status = it.status,
                        downloadFormat = it.downloadFormat,
                        videoLocation = it.videoLocation,
                        downloadOption = it.downloadOption
                    )
                }
            }.awaitAll()
        }
    }

    suspend fun mapFetchFormatToDownloadFormat(
        videoFormats: List<VideoFormat>
    ): List<DownloadFormat> {
        return withContext(Dispatchers.Default) {
            videoFormats.sortedByDescending { it.fileSize }.asSequence()
                .filter { it.formatId != null }
                .filter { it.vcodec != null && it.acodec != null }
                .filter { it.fileSize != 0L && it.format != null }
                .filter { (it.acodec != "none" && it.vcodec == "none") || (it.vcodec != "none" && it.acodec == "none") }
                .map { format ->
                    DownloadFormat(
                        downloadableType = if (format.acodec == "none") {
                            VideoAudioType.VIDEO
                        } else {
                            VideoAudioType.AUDIO
                        },
                        formatId = format.formatId!!,
                        formatNote = format.formatNote ?: "UNKNOWN",
                        decoder = if (format.acodec == "none") {
                            format.vcodec!!
                        } else {
                            format.acodec!!
                        },
                        fileSize = FileUtils.convertFileSize(format.fileSize),
                        externalType = format.ext ?: "Unknown"
                    )
                }.toList()
        }
    }


}