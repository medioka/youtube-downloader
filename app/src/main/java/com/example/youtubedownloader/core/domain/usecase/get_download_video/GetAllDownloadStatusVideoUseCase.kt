package com.example.youtubedownloader.core.domain.usecase.get_download_video

data class GetAllDownloadStatusVideoUseCase(
    val getDownloadingVideoUseCase: GetDownloadingVideoUseCase,
    val getErrorVideoUseCase: GetErrorVideoUseCase,
    val getFinishedVideoUseCase: GetFinishedVideoUseCase,
    val getQueueingVideoUseCase: GetQueueingVideoUseCase
)