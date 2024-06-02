package com.example.youtubedownloader.core.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FileUtils {
    fun convertFileSize(s: Long): String {
        if (s <= 1) return "?"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(s.toDouble()) / log10(1024.0)).toInt()
        val symbols = DecimalFormatSymbols(Locale.US)
        return "${
            DecimalFormat(
                "#,##0.#",
                symbols
            ).format(s / 1024.0.pow(digitGroups.toDouble()))
        } ${units[digitGroups]}"
    }

    fun convertToFileFormat(path: String): String {
        var dataValue = path
        if (dataValue.startsWith("/storage/")) return dataValue
        dataValue = dataValue.replace("content://com.android.externalstorage.documents/tree/", "")
        dataValue = dataValue.replace("raw:/storage/", "")
        dataValue = dataValue.replace("^/document/".toRegex(), "")
        dataValue = dataValue.replace("^primary:".toRegex(), "primary/")
        dataValue = dataValue.replace("%3A".toRegex(), "/")
        try {
            dataValue = URLDecoder.decode(dataValue, StandardCharsets.UTF_8.name())
        } catch (ignored: Exception) {
        }
        val pieces = dataValue.split("/").toTypedArray()
        val formattedPath = StringBuilder("/storage/")
        if (pieces[0] == "primary") {
            formattedPath.append("emulated/0/")
        } else {
            formattedPath.append(pieces[0]).append("/")
        }
        pieces.forEachIndexed { i, it ->
            if (i > 0 && it.isNotEmpty()) {
                formattedPath.append(it).append("/")
            }
        }
        return formattedPath.removeSuffix("/").toString()
    }


    fun getPathByType(type: VideoAudioType): String {
        return if (type == VideoAudioType.VIDEO) getDefaultVideoPath()
        else getDefaultAudioPath()
    }

    private fun getDefaultAudioPath(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "YtDL/Audio"
    }

    private fun getDefaultVideoPath(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "YtDL/Video"
    }

    fun setupTempFolder(video: YtVideo, context: Context): String {
        val targetPath =
            context.filesDir.absolutePath + "/${video.videoId}_${video.downloadFormat!!.formatId}"
        val tempDirectory = File(targetPath)
        if (tempDirectory.exists()) {
            tempDirectory.delete()
        }
        tempDirectory.mkdirs()
        return targetPath
    }

    fun createFolder(path: String) {
        val fileDirectory = File(path)
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
    }

    suspend fun moveFileFromTempToDestination(
        video: YtVideo,
        context: Context,
        response: String
    ): YtVideo? {
        val sourcePath = parseResponseToFilePath(response)
        val fileName = sourcePath.substringAfterLast('/')
        val targetPath = video.videoLocation + "/$fileName"

        return withContext(Dispatchers.IO) {
            try {
                File(sourcePath).let { sourceFile ->
                    sourceFile.copyTo(File(targetPath))
                    video.videoLocation = targetPath
                    sourceFile.delete()
                }
                MediaScannerConnection.scanFile(context, arrayOf(targetPath), null, null)
                video
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseResponseToFilePath(outputResponse: String): String {
        val finalPaths = outputResponse.split("\n")
        val newList = ArrayList<String>()
        for (value in finalPaths) {
            if (value.isEmpty()) continue
            newList.add(value)
        }
        val lastString = newList.last()
        return lastString.substringAfter('\"').removeSuffix("\"")
    }

    suspend fun isFileAlreadyDownloaded(video: YtVideo): Boolean {
        return getExistedFile(video) != null
    }

    suspend fun getExistedFile(video: YtVideo): String? {
        val path = video.downloadFormat?.let { getPathByType(it.downloadableType) } ?: return null
        return withContext(Dispatchers.IO) {
            val newFile = File(path)
            val videos = newFile.list()?.find { it.contains(video.title) }
            videos?.let {
                path + "/" + videos
            }
        }
    }
}