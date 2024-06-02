package com.example.youtubedownloader.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.custom.ResultVideoListener
import com.example.youtubedownloader.databinding.FragmentFavoriteVideoBinding
import com.example.youtubedownloader.format_selector.DownloadConfigurationBottomFragment
import com.example.youtubedownloader.format_selector.viewmodel.ResultViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteVideoFragment : Fragment(), ResultVideoListener {
    private val favoriteViewModel by viewModel<FavoriteViewModel>()
    private val resultViewModel by activityViewModel<ResultViewModel>()
    private var _binding: FragmentFavoriteVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteVideoBinding.inflate(inflater, container, false)
        adapter = FavoriteAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarFavorite.setupWithNavController(findNavController())
        setupSearchBar()
        setupRecyclerView()
    }


    private fun setupSearchBar() {
        binding.searchview.clearFocus()
        binding.searchview.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        adapter.filter(newText)
                    }
                    return true
                }
            }
        )
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(this)
        binding.rvVideoFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVideoFavorite.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.videos.collectLatest { result ->
                    setupVisibility(result)
                }
            }
        }
    }

    private fun setupVisibility(result: Resource<List<YtVideo>>) {
        binding.viewLoading.loading.isVisible = result is Resource.Loading
        binding.viewError.tvErrorMessage.apply {
            isVisible = (result is Resource.Error)
            text = result.exception.toString()
        }
        binding.rvVideoFavorite.apply {
            this@FavoriteVideoFragment.adapter.modifyList(result.data)
            val currentList = this@FavoriteVideoFragment.adapter.currentList
            isVisible =
                result is Resource.Success
        }
    }


    override fun onClickCard(video: YtVideo) {
        val onClickArgs = FavoriteVideoFragmentDirections
            .actionFavoriteVideoFragmentToVideoPlayerBottomFragment(video.videoId)
        findNavController().navigate(onClickArgs)
    }

    override fun onClickFavorite(video: YtVideo) {
        favoriteViewModel.removeVideoFromFavorite(video)
    }

    override fun onClickDownload(video: YtVideo) {
        resultViewModel.addVideo(video)
        val bundle = bundleOf(Pair(DownloadConfigurationBottomFragment.TAG, video.videoId))
        findNavController().navigate(
            R.id.action_favoriteVideoFragment_to_nav_download_config,
            bundle
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "Favorite_Fragment"
    }
}