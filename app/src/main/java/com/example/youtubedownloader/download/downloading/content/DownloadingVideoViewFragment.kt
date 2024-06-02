package com.example.youtubedownloader.download.downloading.content

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.DownloadProcess
import com.example.youtubedownloader.common.MoreOptionBottomSheet
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.custom.DownloadingVideoListener
import com.example.youtubedownloader.databinding.FragmentDownloadingVideoViewBinding
import com.example.youtubedownloader.download.DownloadViewModel
import com.example.youtubedownloader.download.downloading.DownloadingVideoFragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class DownloadingVideoViewFragment : Fragment(), DownloadingVideoListener {
    private var _binding: FragmentDownloadingVideoViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DownloadingContentAdapter
    private val downloadViewModel by activityViewModel<DownloadViewModel>()
    private val downloadProcess by inject<DownloadProcess>()
    private val screen: String
        get() = requireArguments().getString(SCREEN)
            ?: throw IllegalArgumentException("Argument $SCREEN required")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadingVideoViewBinding.inflate(inflater, container, false)
        adapter = DownloadingContentAdapter(this, screen)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DownloadingVideoViewFragment.adapter
        }
        setupAdapterContent()
    }

    private fun setupAdapterContent() {
        when (screen) {
            DownloadingVideoFragment.pageList[0] -> {
                setupDownloadListener(downloadViewModel.queueingVideo)
            }

            DownloadingVideoFragment.pageList[1] -> {
                setupDownloadListener(downloadViewModel.downloadingVideo)
            }

            DownloadingVideoFragment.pageList[2] -> {
                setupDownloadListener(downloadViewModel.errorVideo)
            }
        }
    }

    private fun setupDownloadListener(downloadList: StateFlow<Resource<List<YtVideo>>>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                downloadList.collectLatest {
                    binding.viewLoading.loading.isVisible = it is Resource.Loading
                    binding.viewError.tvErrorMessage.isVisible = it is Resource.Error
                    binding.rvItems.apply {
                        isVisible = it is Resource.Success
                        this@DownloadingVideoViewFragment.adapter.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.viewError.tvErrorMessage.setText("Empty or error")
                            binding.viewError.tvErrorMessage.isVisible = it !is Resource.Loading
                            isVisible = false
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onLongClickCard(video: YtVideo) {
        downloadViewModel.selectVideo(video)
        val modal = MoreOptionBottomSheet()
        modal.show(parentFragmentManager, MoreOptionBottomSheet.TAG)
    }

    override fun updateVideoPercentage(
        percentageUpdater: (Float) -> Unit,
        descriptionUpdater: (String) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    downloadProcess.progress.collect {
                        percentageUpdater(it)
                    }
                }

                launch {
                    downloadProcess.progressDescription.collect {
                        descriptionUpdater(it)
                    }
                }
            }
        }


    }

    companion object {
        private const val TAG = "Download content fragment"
        private const val SCREEN = "screen"

        fun newInstance(screen: String) = DownloadingVideoViewFragment().apply {
            arguments = bundleOf(
                SCREEN to screen,
            )
        }

    }
}