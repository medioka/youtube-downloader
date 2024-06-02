package com.example.youtubedownloader.download.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubedownloader.MainActivity
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.core.domain.Resource
import com.example.youtubedownloader.core.ui.SwipeGesture
import com.example.youtubedownloader.core.utils.DataFormatter
import com.example.youtubedownloader.custom.ClickAndShareListener
import com.example.youtubedownloader.custom.CustomDialog
import com.example.youtubedownloader.databinding.FragmentFinishedVideoBinding
import com.example.youtubedownloader.download.DownloadViewModel
import com.example.youtubedownloader.settings.SettingsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class FinishedVideoFragment : Fragment(), ClickAndShareListener {
    private var _binding: FragmentFinishedVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FinishedAdapter
    private val downloadViewModel by activityViewModel<DownloadViewModel>()
    private lateinit var customDialog: CustomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
        mainActivity.resetDownloadedBadgeCount()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FinishedAdapter(this)
        customDialog = CustomDialog(requireActivity())
        configureSwipeToDelete()
        initRecyclerView()
        toolbarBuilder()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initRecyclerView() {
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                downloadViewModel.finishedVideo.collectLatest {
                    binding.viewLoading.loading.isVisible = it is Resource.Loading
                    binding.viewError.tvErrorMessage.isVisible = it is Resource.Error
                    binding.rvVideos.apply {
                        isVisible = it is Resource.Success
                        this@FinishedVideoFragment.adapter.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.viewError.tvErrorMessage.text = "Empty or error"
                            binding.viewError.tvErrorMessage.isVisible = it !is Resource.Loading
                            isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun toolbarBuilder() {
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment,
            R.id.finishedVideoFragment
        ).build()
        binding.toolbarDownloaded.inflateMenu(R.menu.menu_default)
        binding.toolbarDownloaded.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    val intent = Intent(requireActivity(), SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_downloading -> {
                    findNavController().navigate(R.id.action_finishedVideoFragment_to_downloadingVideoFragment)
                    true
                }

                R.id.action_favorite -> {
                    findNavController().navigate(R.id.action_finishedVideoFragment_to_nav_favorite)
                    true
                }

                else -> false
            }
        }

        binding.toolbarDownloaded.setupWithNavController(findNavController(), appBarConfiguration)
    }

    private fun configureSwipeToDelete() {
        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val currentItem = adapter.currentList[position]
                adapter.notifyItemChanged(position)
                val onDeleteAction = { isAlsoDeleteFile: Boolean ->
                    downloadViewModel.selectVideo(currentItem)
                    downloadViewModel.removeFromDownload(isAlsoDeleteFile)
                }
                customDialog.confirmationDeleteDialog(onDeleteAction)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.rvVideos)
    }

    override fun onClickCard(video: YtVideo) {
        downloadViewModel.selectVideo(video)
        findNavController().navigate(R.id.action_finishedVideoFragment_to_videoDescriptionBottomSheet)
    }

    override fun onShareClick(id: String) {
        val fixedLink = DataFormatter.convertUrlToYoutubeFormat(id)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, fixedLink)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share link")
        startActivity(shareIntent)
    }

    companion object {
        const val TAG = "Downloaded fragment"
    }
}
