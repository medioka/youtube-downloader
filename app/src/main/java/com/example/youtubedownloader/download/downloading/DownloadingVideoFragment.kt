package com.example.youtubedownloader.download.downloading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.FragmentDownloadingVideoBinding
import com.google.android.material.tabs.TabLayoutMediator


class DownloadingVideoFragment : Fragment() {
    private var _binding: FragmentDownloadingVideoBinding? = null
    private lateinit var adapter: DownloadingAdapter
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadingVideoBinding.inflate(inflater, container, false)
        adapter = DownloadingAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarBuilder()
        setupTabLayout()
    }

    private fun setupTabLayout() {
        binding.viewPager.adapter = adapter
        val tabLayout = binding.tabLayoutDownload
        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            tab.text = pageList[position]
        }.attach()
    }

    private fun toolbarBuilder() {
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment,
            R.id.finishedVideoFragment
        ).build()
        binding.toolbarDownloading.inflateMenu(R.menu.menu_default)
        binding.toolbarDownloading.menu.clear()
        binding.toolbarDownloading.setupWithNavController(findNavController(), appBarConfiguration)
    }

    companion object {
        const val PAGE_SIZE = 3
        val pageList = listOf("Queueing", "Downloading", "Error")
    }
}