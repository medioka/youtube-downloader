package com.example.youtubedownloader.di

import com.example.youtubedownloader.core.domain.usecase.modify_result_video.AddResultVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_result_video.DeleteVideoFromFavoriteUseCase
import com.example.youtubedownloader.core.domain.usecase.common.DownloadLogicUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetFavoriteVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetResultVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_result_video.GetVideoBySearchUseCase
import com.example.youtubedownloader.core.domain.usecase.common.GetVideoInfoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_result_video.UpdateResultVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetAllDownloadStatusVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetDownloadingVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetErrorVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetFinishedVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.get_download_video.GetQueueingVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.AddToDownloadVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.ModifyDownloadVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.RemoveDownloadVideoUseCase
import com.example.youtubedownloader.core.domain.usecase.modify_download_video.UpdateDownloadVideoUseCase
import org.koin.dsl.module


private val getVideoBySearchUseCase = module {
    factory { GetVideoBySearchUseCase(get()) }
}
private val addResultVideoUseCase = module {
    factory { AddResultVideoUseCase(get(), get(), get()) }
}
private val deleteVideoFromFavoriteUseCase = module {
    factory { DeleteVideoFromFavoriteUseCase(get()) }
}
private val getVideoFromFavoriteUseCase = module {
    factory { GetFavoriteVideoUseCase(get()) }
}

private val getResultVideoUseCase = module {
    factory { GetResultVideoUseCase(get()) }
}

private val getVideoInfoUseCase = module {
    factory { GetVideoInfoUseCase(get()) }
}
private val updateVideoUseCase = module {
    factory { UpdateResultVideoUseCase(get()) }
}

private val getDownloadVideoUseCase = module {
    factory { GetAllDownloadStatusVideoUseCase(get(), get(), get(), get()) }
    factory { GetQueueingVideoUseCase(get()) }
    factory { GetFinishedVideoUseCase(get()) }
    factory { GetErrorVideoUseCase(get()) }
    factory { GetDownloadingVideoUseCase(get()) }
}

private val modifyDownloadVideoUseCase = module {
    factory { ModifyDownloadVideoUseCase(get(), get(), get()) }
    factory { AddToDownloadVideoUseCase(get()) }
    factory { RemoveDownloadVideoUseCase(get()) }
    factory { UpdateDownloadVideoUseCase(get()) }
}

private val downloadLogicUseCase = module {
    single { DownloadLogicUseCase(get(), get()) }
}


val useCaseModule = module {
    includes(
        getVideoBySearchUseCase,
        addResultVideoUseCase,
        deleteVideoFromFavoriteUseCase,
        getVideoFromFavoriteUseCase,
        getResultVideoUseCase,
        getVideoInfoUseCase,
        updateVideoUseCase,
        getDownloadVideoUseCase,
        modifyDownloadVideoUseCase,
        downloadLogicUseCase
    )
}