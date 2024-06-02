package com.example.youtubedownloader.format_selector.format

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.domain.enums.VideoAudioType
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.core.utils.DefaultValueUtils
import com.example.youtubedownloader.custom.FormatClickListener
import com.example.youtubedownloader.databinding.DialogFormatSelectorBinding
import com.example.youtubedownloader.format_selector.viewmodel.ResultViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FormatSelectorFragment : BottomSheetDialogFragment(), FormatClickListener {
    private lateinit var binding: DialogFormatSelectorBinding
    private val resultViewModel by activityViewModel<ResultViewModel>()
    private val formatSelectorViewModel by viewModel<FormatSelectorViewModel>()
    private lateinit var defaultItems: List<DownloadFormat>
    private lateinit var adapter: FormatSelectionAdapter
    private var job: Job? = null

    private val args: FormatSelectorFragmentArgs by navArgs()
    private val contentId: String
        get() = args.id

    private val type: VideoAudioType
        get() = args.type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFormatSelectorBinding.inflate(inflater, container, false)
        defaultItems = DefaultValueUtils.getDefaultValueByType(type)
        adapter = FormatSelectionAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCustom.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCustom.adapter = adapter
        initResultListener()
    }

    private fun initResultListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val isInitialValueAvailable = lifecycleScope.async { setupInitialFormat() }
                if (isInitialValueAvailable.await()) {
                    cancel()
                    return@repeatOnLifecycle
                }
                resultViewModel.resultVideos.collectLatest { video ->
                    val itemFormats = video
                        .find { it.videoId == contentId }
                        ?.downloadableFormatList
                    if (itemFormats.isNullOrEmpty()) {
                        setupOkButton()
                        return@collectLatest
                    }
                    formatSelectorViewModel.updateList(itemFormats)
                    setupUpdateButton()
                    cancel()
                }
            }
        }
    }

    private fun setupUpdateButton() {
        job?.cancel()
        binding.btnOk.apply {
            isEnabled = true
            icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.icon_update
            )
            text = context.getString(R.string.update_video)
            setOnClickListener {
                val newList = formatSelectorViewModel.newList.value
                formatSelectorViewModel.updateCurrentItem(null)
                isEnabled = false
                adapter.submitList(newList) { isEnabled = true; setupOkButton() }
            }
        }
    }

    override fun onFormatClickListener(downloadFormat: DownloadFormat) {
        formatSelectorViewModel.updateCurrentItem(downloadFormat)
    }

    private fun setupInitialFormat(): Boolean {
        val itemFormats = resultViewModel.getInitialFormat(contentId, type)

        //Checking itemFormats to determine which data to use to populate
        if (!itemFormats.isNullOrEmpty()) {
            adapter.submitList(itemFormats)
            setupOkButton()
            return true
        }
        adapter.submitList(defaultItems)
        return false
    }

    private fun setupOkButton() {
        binding.btnOk.apply {
            icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.icon_check
            )
            text = getString(R.string.ok)
        }
        job = viewLifecycleOwner.lifecycleScope.launch {
            formatSelectorViewModel.currentItem.collect { itemFormat ->
                binding.btnOk.isEnabled = itemFormat != null
                binding.btnOk.setOnClickListener {
                    itemFormat?.let { selectedFormat ->
                        setupFragmentResult(selectedFormat)
                        dismiss()
                    }

                }
            }
        }
    }

    private fun setupFragmentResult(selectedFormat: DownloadFormat) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set("key", selectedFormat)
    }


    companion object {
        const val TAG = "Format Selection Fragment"
    }

}