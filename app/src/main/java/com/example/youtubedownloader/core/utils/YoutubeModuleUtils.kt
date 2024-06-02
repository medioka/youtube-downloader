package com.example.youtubedownloader.core.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.youtubedownloader.settings.SettingsFragment
import com.yausername.youtubedl_android.YoutubeDL

class YoutubeModuleUtils(private val context: Context) {
    private val availableUpdateChannel = mapOf(
        "nightly" to YoutubeDL.UpdateChannel._NIGHTLY,
        "stable" to YoutubeDL.UpdateChannel._STABLE
    )
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    fun updateYoutubeModule(): YoutubeDL.UpdateStatus? {
        val updateChannelKey = context.getString(SettingsFragment.updateChannelKey)
        val updateChannel = sharedPreferences.getString(updateChannelKey, "nightly")
        return YoutubeDL.updateYoutubeDL(
            context,
            availableUpdateChannel[updateChannel]
                ?: YoutubeDL.UpdateChannel._NIGHTLY
        )
    }

    fun getCurrentModuleVersion(): String? {
        return YoutubeDL.getInstance().version(context)
    }

    fun updateCurrentModuleVersion(version: String) {
        val versionKey = context.getString(SettingsFragment.versionKey)
        sharedPreferences.edit().putString(versionKey, version).apply()
    }

    fun getAutoUpdateStatus(): Boolean {
        val autoUpdateKey = context.getString(SettingsFragment.autoUpdateKey)
        return sharedPreferences.getBoolean(autoUpdateKey, true)
    }
}