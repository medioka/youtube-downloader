package com.example.youtubedownloader.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.ui.PagingAdapter
import com.example.youtubedownloader.core.ui.PagingLoadStateAdapter
import com.example.youtubedownloader.custom.ResultVideoListener
import com.example.youtubedownloader.databinding.FragmentHomeBinding
import com.example.youtubedownloader.format_selector.DownloadConfigurationBottomFragment
import com.example.youtubedownloader.format_selector.viewmodel.ResultViewModel
import com.example.youtubedownloader.settings.SettingsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), ResultVideoListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val homeViewModel by viewModel<HomeViewModel>()
    private val resultViewModel by activityViewModel<ResultViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        toolbarBuilder()
        setupSearchBar()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val pagingAdapter = PagingAdapter(this)
        binding.homeRvVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRvVideos.adapter = pagingAdapter.withLoadStateFooter(
            //LOADING STATE LISTENER IN RECYCLERVIEW
            footer = PagingLoadStateAdapter(pagingAdapter::retry)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingAdapter.loadStateFlow.collect {
                    binding.viewLoading.loading.isVisible = it.refresh is LoadState.Loading
                    binding.homeRvVideos.isVisible = it.refresh is LoadState.NotLoading
                    binding.viewError.tvErrorMessage.apply {
                        isVisible = it.refresh is LoadState.Error
                        val refresh = it.refresh
                        if (refresh is LoadState.Error) {
                            text = refresh.error.toString()
                        }
                    }
                    binding.viewError.btnRetry.apply {
                        isVisible = it.refresh is LoadState.Error
                        setOnClickListener { pagingAdapter.retry() }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.paginationFlow.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }
    }


    private fun toolbarBuilder() {
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment,
            R.id.finishedVideoFragment
        ).build()
        binding.toolbarMain.inflateMenu(R.menu.menu_default)
        binding.toolbarMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    val intent = Intent(requireActivity(), SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_downloading -> {
                    navController.navigate(R.id.action_homeFragment_to_downloadingVideoFragment)
                    true
                }

                R.id.action_favorite -> {
                    navController.navigate(R.id.action_homeFragment_to_nav_favorite)
                    true
                }

                else -> false
            }
        }

        binding.toolbarMain.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupSearchBar() {
        binding.searchview.setupWithSearchBar(binding.searchbar)
        binding.searchview
            .editText
            .setOnEditorActionListener { _, _, _ ->
                val text = binding.searchview.text.toString()
                binding.searchbar.setText(binding.searchview.text)
                binding.searchview.hide()
                homeViewModel.updateSearchQuery(text)
                false
            }
    }

    override fun onClickCard(video: YtVideo) {
        val onClickArgs =
            HomeFragmentDirections.actionHomeFragmentToVideoPlayerBottomFragment(video.videoId)
        findNavController().navigate(onClickArgs)
    }

    override fun onClickFavorite(video: YtVideo) {
        homeViewModel.addVideoToFavorite(video)
    }

    override fun onClickDownload(video: YtVideo) {
        resultViewModel.addVideo(video)
        val bundle = bundleOf(
            Pair(DownloadConfigurationBottomFragment.TAG, video.videoId),
        )
        findNavController().navigate(
            R.id.action_homeFragment_to_nav_download_config,
            bundle
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "Home_Fragment"
    }
}