package com.example.youtubedownloader

import android.app.Application
import com.example.youtubedownloader.core.di.coreModule
import com.example.youtubedownloader.di.downloadProcessModule
import com.example.youtubedownloader.di.useCaseModule
import com.example.youtubedownloader.di.viewModelModule
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@App)
            modules(
                coreModule,
                viewModelModule,
                useCaseModule,
                downloadProcessModule
            )
        }
        setupYtLibrary()
    }


    private fun setupYtLibrary() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(this@App)
                Aria2c.getInstance().init(this@App)
                FFmpeg.getInstance().init(this@App)
            } catch (e: Exception) {
//                val dialogUtils = DialogUtils(applicationContext)
//                dialogUtils.createToast(e.toString())
            }
        }
    }
}