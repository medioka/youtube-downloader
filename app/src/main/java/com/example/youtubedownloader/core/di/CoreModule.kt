package com.example.youtubedownloader.core.di

import androidx.room.Room
import com.example.youtubedownloader.core.data.local.LocalDataSource
import com.example.youtubedownloader.core.data.local.database.YtDatabase
import com.example.youtubedownloader.core.data.remote.RemoteDataSource
import com.example.youtubedownloader.core.data.remote.RemoteDataSourceImpl
import com.example.youtubedownloader.core.data.remote.retrofit.YoutubeApiService
import com.example.youtubedownloader.core.data.repository.DownloadRepository
import com.example.youtubedownloader.core.data.repository.ResultRepository
import com.example.youtubedownloader.core.domain.repository.IDownloadRepository
import com.example.youtubedownloader.core.domain.repository.IResultRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(YoutubeApiService.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(YoutubeApiService::class.java)
    }
}

private val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            YtDatabase::class.java,
            "yt.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    factory {
        get<YtDatabase>().downloadDao()
    }
    factory {
        get<YtDatabase>().resultDao()
    }
}

private val repositoryModule = module {
    single {
        LocalDataSource(get(), get())
    }
    single<RemoteDataSource> {
        RemoteDataSourceImpl(get())
    }

    single<IResultRepository> { ResultRepository(get(), get()) }

    single<IDownloadRepository> { DownloadRepository(get()) }
}

val coreModule = module {
    includes(
        networkModule,
        databaseModule,
        repositoryModule
    )
}