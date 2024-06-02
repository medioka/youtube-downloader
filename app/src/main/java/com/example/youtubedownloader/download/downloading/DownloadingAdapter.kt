package com.example.youtubedownloader.download.downloading

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.youtubedownloader.download.downloading.content.DownloadingVideoViewFragment

class DownloadingAdapter(fragment: DownloadingVideoFragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return DownloadingVideoFragment.PAGE_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        return DownloadingVideoViewFragment.newInstance(screen = DownloadingVideoFragment.pageList[position])
    }
}