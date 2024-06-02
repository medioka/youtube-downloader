package com.example.youtubedownloader.core.domain.usecase.modify_download_video

data class ModifyDownloadVideoUseCase(
    val addToDownloadVideoUseCase: AddToDownloadVideoUseCase,
    val removeDownloadVideoUseCase: RemoveDownloadVideoUseCase,
    val updateDownloadVideoUseCase: UpdateDownloadVideoUseCase
)