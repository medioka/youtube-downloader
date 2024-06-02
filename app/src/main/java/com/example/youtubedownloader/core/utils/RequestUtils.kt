package com.example.youtubedownloader.core.utils

import android.app.Application
import android.util.Log
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

object RequestUtils {
    private var request: YoutubeDLRequest? = null
    private const val TAG = "Request Utils"
    suspend fun createDownloadRequest(video: YtVideo, application: Application): YoutubeDLRequest {
        println(video)
        request = null
        val url = DataFormatter.convertUrlToYoutubeFormat(video.videoId)
        request = YoutubeDLRequest(url).apply {
            addOption("--no-mtime")
            addOption("--embed-metadata")
            addOption("--downloader", "libaria2c.so")
            addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"");
        }
        setMergingWhenVideoType(video)
        setVideoNaming(video, application)
        addThumbnailToCoverArts()
        return request!!
    }

    private fun addThumbnailToCoverArts() {
        request?.addOption("--embed-thumbnail")
    }

    private fun setVideoNaming(
        video: YtVideo,
        application: Application
    ) {
        if (video.videoLocation.isEmpty()) return
        val parentPath = FileUtils.setupTempFolder(video, application)
        val title = video.title
        FileUtils.createFolder(parentPath)
        request?.addCommands(listOf("--replace-in-metadata", "title", ".+", title))
        request?.addOption("-o", "$parentPath/%(title)s.%(ext)s")
    }

    private fun setMergingWhenVideoType(
        video: YtVideo
    ) {

        Log.d(TAG, video.downloadFormat!!.externalType.toString())
        when (video.downloadFormat!!.downloadableType) {
            VideoAudioType.VIDEO -> handleVideoType(video)
            VideoAudioType.DEFAULT_VIDEO -> handleDefaultVideoType(video)
            VideoAudioType.AUDIO -> handleAudioType(video)
            VideoAudioType.DEFAULT_AUDIO -> handleDefaultAudioType(video)
        }
    }

    private fun handleVideoType(video: YtVideo) {
        val container = video.downloadOption.container
        request?.addOption("-f", "${video.downloadFormat!!.formatId}+ba")
        handleVideoContainer(container)
    }

    private fun handleDefaultVideoType(video: YtVideo) {
        val formatNote = video.downloadFormat!!.formatNote
        val container = video.downloadOption.container
        request?.addOption("-S", "res:$formatNote")
        handleVideoContainer(container)
    }

    private fun handleAudioType(video: YtVideo) {
        val formatId = video.downloadFormat!!.formatId
        val container = video.downloadOption.container
        request?.addOption("-f", formatId)
        handleAudioContainer(container, video.videoLocation)
    }

    private fun handleDefaultAudioType(video: YtVideo) {
        val quality = video.downloadFormat!!.decoder
        val container = video.downloadOption.container
        Log.d(TAG, quality)
        request?.addOption("-f", quality)
        handleAudioContainer(container, video.videoLocation)
    }

    private fun handleVideoContainer(container: String) {
        if (container == "DEFAULT") return
        request?.addOption(
            "--remux-video",
            container.lowercase()
        )
    }

    private fun handleAudioContainer(container: String, videoLocation: String) {
        val config = File("$videoLocation/##ffmpegCrop.txt")
        val configData =
            "--ppa \"ffmpeg:-c:v mjpeg -vf crop=\\\"'if(gt(ih,iw),iw,ih)':'if(gt(iw,ih),ih,iw)'\\\"\""
        config.writeText(configData)
        request?.addOption("--convert-thumbnails", "jpg")
        request?.addOption("--ppa", "ThumbnailsConvertor:-qmin 1 -q:v 1")
        request?.addOption("--config", config.absolutePath)
        request?.addOption("--extract-audio")

        if (container == "DEFAULT") return
        request?.addOption(
            "--audio-format",
            container.lowercase()
        )
    }


}