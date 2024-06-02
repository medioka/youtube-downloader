package com.example.youtubedownloader.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.host_setting, SettingsFragment())
            .commit()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        if (supportFragmentManager.backStackEntryCount == 0) {
            binding.toolbarSettings.setNavigationOnClickListener {
                finish()
            }
            title = getString(R.string.settings)
        } else {
            binding.toolbarSettings.setNavigationOnClickListener {
                supportFragmentManager.popBackStackImmediate()
            }
        }
    }


    companion object {
        private val TAG = "Settings activity"
    }
}