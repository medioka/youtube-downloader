package com.example.youtubedownloader.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.youtubedownloader.MainActivity
import com.example.youtubedownloader.R
import com.example.youtubedownloader.custom.LoadingDialog
import com.example.youtubedownloader.settings.ThemeMode
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UiUtils {

    fun copyYoutubeLink(videoLink: String, context: Context) {
        val dialogUtils = DialogUtils(context)
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clipData = ClipData.newPlainText("text", videoLink)
        clipboardManager?.setPrimaryClip(clipData)
        dialogUtils.createToast("Video link copied")
    }

    fun setupAppTheme(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val themeKey =
            preferences.getString(context.getString(R.string.key_theme), "System")!!
        when (ThemeMode.valueOf(themeKey)) {
            ThemeMode.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            ThemeMode.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun checkIsModuleUpdated(activity: MainActivity, view: View, anchorView: View) {
        val moduleUtils = YoutubeModuleUtils(activity)
        val dialogUtils = DialogUtils(activity)
        val isAutoUpdated = moduleUtils.getAutoUpdateStatus()
        val isVersionExist = !moduleUtils.getCurrentModuleVersion().isNullOrEmpty()
        if (!isAutoUpdated && isVersionExist) return
        val loadingDialog = LoadingDialog(activity)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                loadingDialog.startDialog()
                val status = withContext(Dispatchers.IO) { moduleUtils.updateYoutubeModule() }
                if (status == YoutubeDL.UpdateStatus.DONE) {
                    val currentVersion = YoutubeDL.getInstance().version(activity)
                    currentVersion?.let { moduleUtils.updateCurrentModuleVersion(it) }
                    dialogUtils.createToast(currentVersion ?: "Updated")
                }
            } catch (e: Exception) {
                dialogUtils.createToast("Error : $e")
                if (!isVersionExist) dialogUtils.createSnackBar(view, anchorView)
            } finally {
                loadingDialog.closeDialog()
            }
        }
    }
}