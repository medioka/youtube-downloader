package com.example.youtubedownloader.di

import com.example.youtubedownloader.DownloadProcess
import com.example.youtubedownloader.download.DownloadViewModel
import com.example.youtubedownloader.favorite.FavoriteViewModel
import com.example.youtubedownloader.format_selector.DownloadConfigViewModel
import com.example.youtubedownloader.format_selector.format.FormatSelectorViewModel
import com.example.youtubedownloader.format_selector.viewmodel.ResultViewModel
import com.example.youtubedownloader.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val homeViewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
}
private val favoriteViewModel = module {
    viewModel { FavoriteViewModel(get(), get()) }
}

private val resultViewModel = module {
    viewModel { ResultViewModel(get(), get(), get()) }

}

private val homeViewModel = module {
    viewModel { HomeViewModel(get(), get()) }
}

private val formatSelectorViewModel = module {
    viewModel { FormatSelectorViewModel() }
}

private val downloadViewModel = module {
    viewModel { DownloadViewModel(get(), get()) }
}

private val downloadConfigViewModel = module {
    viewModel { DownloadConfigViewModel(get()) }
}

val downloadProcessModule = module {
    single { DownloadProcess(get(), get(), get()) }
}


val viewModelModule = module {
    includes(
        homeViewModelModule,
        favoriteViewModel,
        resultViewModel,
        homeViewModel,
        downloadConfigViewModel,
        formatSelectorViewModel,
        downloadViewModel,
    )
}