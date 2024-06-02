package com.example.youtubedownloader.format_selector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.utils.DefaultValueUtils
import com.example.youtubedownloader.core.utils.FileUtils
import com.example.youtubedownloader.custom.CustomDialog
import com.example.youtubedownloader.databinding.DialogDownloadConfigBinding
import com.example.youtubedownloader.favorite.FavoriteVideoFragment
import com.example.youtubedownloader.format_selector.viewmodel.ResultViewModel
import com.example.youtubedownloader.home.HomeFragment
import com.example.youtubedownloader.home.HomeFragmentDirections
import com.example.youtubedownloader.player.VideoPlayerBottomFragmentArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DownloadConfigurationBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDownloadConfigBinding
    private val resultViewModel by activityViewModel<ResultViewModel>()
    private val downloadConfigViewModel by viewModel<DownloadConfigViewModel>()
    private lateinit var customDialog: CustomDialog
    private var job: Job? = null
    private var fetchingVideoJob: Job? = null
    private val screen = MutableStateFlow(screenTag[0])
    private val videoId: String
        get() = requireArguments().getString(TAG)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        customDialog = CustomDialog(requireActivity())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDownloadConfigBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            resultViewModel.getVideoInfo(videoId)
            configDefaultLocation()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<DownloadFormat>("key")
            ?.observe(viewLifecycleOwner) { downloadFormat ->
                downloadConfigViewModel.updateDownloadFormat(downloadFormat)
            }
        setupTitleAuthorField()
        setupDownloadButton()
        card()
    }

    private fun configDefaultLocation() {
        val audioPath = FileUtils.getPathByType(VideoAudioType.AUDIO)
        val videoPath = FileUtils.getPathByType(VideoAudioType.VIDEO)
        downloadConfigViewModel.updatePathBasedOnType(VideoAudioType.VIDEO, videoPath)
        downloadConfigViewModel.updatePathBasedOnType(VideoAudioType.AUDIO, audioPath)
    }

    private fun setupTitleAuthorField() {
        val video =
            downloadConfigViewModel.currentVideo.value ?: resultViewModel.currentVideo.value!!
        downloadConfigViewModel.updateCurrentVideo(video)
        binding.textfieldTitleEdit.setText(video.title)
        binding.textfieldAuthorEdit.setText(video.artist)
    }

    private fun setupDownloadPathInitialValue(type: VideoAudioType) {
        var defaultPath =
            when (type) {
                VideoAudioType.VIDEO -> downloadConfigViewModel.videoPath.value
                VideoAudioType.AUDIO -> downloadConfigViewModel.audioPath.value
                else -> throw IllegalArgumentException("No such screen")
            }
        if (defaultPath.isEmpty()) defaultPath = FileUtils.getPathByType(type)
        binding.sectionDownloadPath.tvDownloadPath.text = defaultPath
        binding.sectionDownloadPath.btnEditPath.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            pathResultLauncher.launch(intent)
        }
    }

    private fun setupDownloadButton() {
        binding.btnDownload.setOnClickListener {
            val type = screen.value.second
            val updatedVideo = downloadConfigViewModel.currentVideo.value!!.copy(
                title = binding.textfieldTitleEdit.text.toString(),
                artist = binding.textfieldAuthorEdit.text.toString(),
            )
            downloadConfigViewModel.addToQueue(updatedVideo, type)
            dismiss()
        }
    }


    private fun card() {
        setupListenerByScreen()
        binding.sectionDownloadFormat.toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            when (checkedId) {
                R.id.btn_video -> screen.update { screenTag[0] }
                R.id.btn_audio -> screen.update { screenTag[1] }
            }
        }
    }

    //LISTENER
    private fun setupListenerByScreen() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                screen.collectLatest {
                    cardListener(it.second)
                }
            }
        }
    }

    private fun cardListener(type: VideoAudioType) {
        initialVideoCardValue(type)
        job?.cancel()
        val observedType = if (type == VideoAudioType.VIDEO) {
            downloadConfigViewModel.currentVideoFormat
        } else {
            downloadConfigViewModel.currentAudioFormat
        }
        setupDownloadPathInitialValue(type)
        setupDropDownItems()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observedType.collectLatest { downloadFormat ->
                    if (downloadFormat == null) return@collectLatest
                    populateCardContent(downloadFormat)
                }
            }
        }
    }

    private fun populateCardContent(downloadFormat: DownloadFormat) {
        val drawable = if (screen.value == screenTag[0]) {
            AppCompatResources.getDrawable(requireContext(), R.drawable.icon_video)
        } else AppCompatResources.getDrawable(requireContext(), R.drawable.icon_audio)

        with(binding.sectionDownloadFormat.cardviewItem) {
            tvDownloadVideoFormat.text = requireContext().getString(
                R.string.description_download_format,
                downloadFormat.externalType,
                downloadFormat.formatNote
            )
            tvType.text = screen.value.first.name
            tvFileSize.text = downloadFormat.fileSize
            iconType.setImageDrawable(drawable)
            card.setOnClickListener {
                val type = screen.value.second
                val onSelectFormatArgs =
                    DownloadConfigurationBottomFragmentDirections
                        .actionDownloadConfigurationBottomFragmentToFormatSelectorFragment(
                            videoId,
                            type
                        )
                findNavController().navigate(onSelectFormatArgs)
            }
        }
    }


    private fun initialVideoCardValue(type: VideoAudioType) {
        val observedType = when (type) {
            VideoAudioType.VIDEO -> downloadConfigViewModel.currentVideoFormat
            VideoAudioType.AUDIO -> downloadConfigViewModel.currentAudioFormat
            else -> throw IllegalArgumentException("No such type")
        }
        val downloadFormat = observedType.value
            ?: resultViewModel.currentVideo.value
                ?.downloadableFormatList
                ?.firstOrNull { it.downloadableType == type }

        if (downloadFormat != null) {
            downloadConfigViewModel.updateDownloadFormat(downloadFormat)
            populateCardContent(downloadFormat)
            return
        }

        //IF DATA HAS NOT YET AVAILABLE
        val defaultFormat = DefaultValueUtils.getDefaultValueByType(type)
        downloadConfigViewModel.updateDownloadFormat(defaultFormat.first())

        //PUT LISTENER to downloadFormat
        fetchingVideoJob?.cancel()
        fetchingVideoJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                resultViewModel.resultVideos.collectLatest { videoList ->
                    val itemFormat = videoList
                        .find { it.videoId == videoId }
                        ?.downloadableFormatList
                        ?.firstOrNull { it.downloadableType == type } ?: return@collectLatest

                    downloadConfigViewModel.updateDownloadFormat(itemFormat)
                    cancel()
                }
            }
        }
    }

    private fun setupDropDownItems() {
        val type = screen.value.second
        val videoContainer = when (type) {
            VideoAudioType.VIDEO -> DefaultValueUtils.getVideoContainer()
            VideoAudioType.AUDIO -> DefaultValueUtils.getAudioContainers()
            else -> throw IllegalArgumentException("No such type")
        }
        val defaultIndex = when (type) {
            VideoAudioType.VIDEO -> downloadConfigViewModel.videoContainerIndex.value
            VideoAudioType.AUDIO -> downloadConfigViewModel.audioContainerIndex.value
            else -> 0
        }
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.item_dropdown, videoContainer)
        val autocompleteTV = binding.sectionDownloadFormat.textfieldContainerEdit
        autocompleteTV.apply {
            setAdapter(arrayAdapter)
            setText(arrayAdapter.getItem(defaultIndex).toString(), false)
            setSelection(autocompleteTV.text.length)
        }
        autocompleteTV.setOnItemClickListener { parent, _, position, _ ->
            parent!!.getItemAtPosition(position).toString()
            downloadConfigViewModel.updateCurrentContainer(
                position = position,
                type = type
            )
        }
    }

    private val pathResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                val path = FileUtils.convertToFileFormat(it.toString())
                binding.sectionDownloadPath.tvDownloadPath.setText(path)
                downloadConfigViewModel.updatePathBasedOnType(screen.value.second, path)
            }
        }
    }

    companion object {
        private const val VIDEO_ID = "videoId"
        const val TAG = "Download_Config"
        val screenTag = listOf(
            Pair(Screen.VIDEO, VideoAudioType.VIDEO),
            Pair(Screen.AUDIO, VideoAudioType.AUDIO)
        )
    }
}

enum class Screen {
    VIDEO,
    AUDIO
}

