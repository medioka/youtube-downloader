package com.example.youtubedownloader.common

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.DialogVideoDescriptionBinding
import com.example.youtubedownloader.download.DownloadViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class VideoDescriptionBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogVideoDescriptionBinding
    private val downloadViewModel by activityViewModel<DownloadViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogVideoDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayButton()
        setupCardView()
    }

    override fun onCancel(dialog: DialogInterface) {
        downloadViewModel.selectVideo(null)
        super.onCancel(dialog)
    }


    private fun setupPlayButton() {
        binding.btnPlayRemove.setOnClickListener {
            val path =
                downloadViewModel.selectedVideo.value?.videoLocation ?: return@setOnClickListener
            val uri = Uri.parse(path)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "video/*")
            startActivity(intent)
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupCardView() {
        val video = downloadViewModel.selectedVideo.value ?: return
        with(binding) {
            Glide.with(this@VideoDescriptionBottomSheet).load(video.thumbnail)
                .error(R.drawable.icon_error_photo)
                .into(imgThumbnail)
            tvVideoTitle.text = video.title
            tvVideoDescription.text =
                getString(R.string.description, video.artist, video.publishedDate)
        }
    }

    companion object {
        const val TAG = "Video description bottom sheet"
    }
}