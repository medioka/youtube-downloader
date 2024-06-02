package com.example.youtubedownloader.core.domain.usecase.common

import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import com.example.youtubedownloader.core.utils.DataFormatter
import com.example.youtubedownloader.core.utils.DataMapper
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetVideoInfoUseCase(private val repository: IResultRepository) {
    operator fun invoke(video: YtVideo): Flow<Resource<List<DownloadFormat>>> {
        return flow {
            try {
                println("Request started")
                val url = DataFormatter.convertUrlToYoutubeFormat(video.videoId)
                val videoAudioList = YoutubeDL.getInstance().getInfo(url)
                val finalList = DataMapper.mapFetchFormatToDownloadFormat(videoAudioList.formats!!)
                repository.update(video.copy(downloadableFormatList = finalList))
                emit(Resource.Success(finalList))
            } catch (e: Exception) {
                println("Error Info: $e")
                emit(Resource.Error(e))
            }
        }.flowOn(Dispatchers.IO)

    }


}