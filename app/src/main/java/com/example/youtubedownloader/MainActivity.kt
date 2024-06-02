package com.example.youtubedownloader

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.youtubedownloader.core.utils.UiUtils
import com.example.youtubedownloader.databinding.ActivityMainBinding
import com.example.youtubedownloader.download.finished.FinishedVideoFragment
import com.google.android.material.badge.BadgeDrawable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var downloadBadge: BadgeDrawable
    private var downloadedCount: Int = 0
    private val downloadCountKey: String = "downloadCount"
    private val downloadProcess by inject<DownloadProcess>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (savedInstanceState == null) {
            UiUtils.setupAppTheme(applicationContext)
            UiUtils.checkIsModuleUpdated(this, binding.root, binding.navBottom)
            downloadProcess
        } else {
            downloadedCount = savedInstanceState.getInt(downloadCountKey, 0) ?: 0
        }
        setContentView(binding.root)

        downloadBadge = binding.navBottom.getOrCreateBadge(R.id.nav_finished).apply {
            isVisible = false
        }
        addDownloadedBadgeCount()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.navBottom.setupWithNavController(navController)
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(downloadCountKey, downloadedCount)
    }

    private fun addDownloadedBadgeCount() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                downloadProcess.downloadCount.collect {
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_main)
                    if (currentFragment != null && currentFragment is FinishedVideoFragment) {
                        resetDownloadedBadgeCount()
                        return@collect
                    }
                    downloadBadge.isVisible = true
                    downloadedCount += it
                    downloadBadge.number = downloadedCount
                }
            }
        }
    }

    fun resetDownloadedBadgeCount() {
        downloadedCount = 0
        downloadBadge.isVisible = false
    }


}