package com.example.youtubedownloader.core.domain.usecase.modify_download_video

import android.util.Log
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.enums.DownloadStatus
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository
import com.example.youtubedownloader.core.utils.FileUtils
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class AddToDownloadVideoUseCase(private val repository: IDownloadRepository) {
    suspend operator fun invoke(video: YtVideo, isFileExist: Boolean): Boolean {

        //TODO IS FILE EXIST
        val readyToDownloadVideo = video.copy(status = DownloadStatus.QUEUEING)

        if (isFileExist) {
            val file = FileUtils.getExistedFile(video)?.let { File(it) }
            repository.getFinishedVideo().firstOrNull()
            file?.delete()
        }

        return try {
            repository.add(readyToDownloadVideo)
            true
        } catch (e: Exception) {
            Log.e("Add use case: ", e.toString())
            false
        }
    }

}