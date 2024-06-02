package com.example.youtubedownloader.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youtubedownloader.core.utils.DataFormatter
import com.example.youtubedownloader.core.utils.UiUtils
import com.example.youtubedownloader.custom.CustomDialog
import com.example.youtubedownloader.databinding.DialogMoreOptionBinding
import com.example.youtubedownloader.download.DownloadViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MoreOptionBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogMoreOptionBinding
    private val downloadViewModel by activityViewModel<DownloadViewModel>()
    private lateinit var customDialog: CustomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        customDialog = CustomDialog(requireActivity())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMoreOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDeleteVideoButton()
        setupCopyVideoLink()
    }

    private fun setupDeleteVideoButton() {
        val onDeleteAction = { isAlsoDeleteFile: Boolean ->
            downloadViewModel.removeFromDownload(isAlsoDeleteFile)
            dismiss()
        }
        binding.actionDeleteVideo.setOnClickListener {
            customDialog.confirmationDeleteDialog(onDeleteAction)
        }
    }

    private fun setupCopyVideoLink() {
        binding.actionCopyLink.setOnClickListener {
            val videoId =
                downloadViewModel.selectedVideo.value?.videoId ?: return@setOnClickListener
            val videoLink = DataFormatter.convertUrlToYoutubeFormat(videoId)
            UiUtils.copyYoutubeLink(videoLink, requireContext())
            dismiss()
        }
    }

    companion object {
        const val TAG = "More option bottom sheet"
    }
}