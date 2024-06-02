package com.example.youtubedownloader.download.downloading.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.custom.DownloadingVideoListener
import com.example.youtubedownloader.databinding.ItemDownloadingVideoBinding


class DownloadingContentAdapter(
    private val downloadListener: DownloadingVideoListener,
    private val screen: String
) : ListAdapter<YtVideo, DownloadingContentAdapter.DownloadingContentViewHolder>(DiffCallback()) {

    class DownloadingContentViewHolder(
        val binding: ItemDownloadingVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context
        fun bind(video: YtVideo) {
            binding.downloadingTvTitle.text = video.title
            binding.downloadingTvDescription.text = context.getString(
                R.string.description,
                video.artist,
                video.publishedDate
            )
            Glide.with(itemView.context).load(video.thumbnail)
                .error(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.icon_error_photo
                    )
                )
                .into(binding.downloadingImgThumbnail)
            bindAdditionalFormat(video)
        }

        private fun bindAdditionalFormat(video: YtVideo) {
            val downloadFormat = video.downloadFormat!!
            with(binding.downloadingAdditionalFormat) {
                tvFilesize.text = downloadFormat.fileSize
                tvExtensionType.text = downloadFormat.externalType
                tvQuality.text = downloadFormat.formatNote
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadingContentViewHolder {
        val binding =
            ItemDownloadingVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DownloadingContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadingContentViewHolder, position: Int) {
        val video = currentList[position]
        holder.bind(video)
        holder.itemView.setOnLongClickListener {
            downloadListener.onLongClickCard(video)
            true
        }
        when (screen) {
            "Downloading" -> setupDownloadVisibility(holder, true)
            else -> setupDownloadVisibility(holder, false)
        }
    }

    private fun setupDownloadVisibility(holder: DownloadingContentViewHolder, isVisible: Boolean) {
        val binding = holder.binding
        binding.downloadingTvProgress.isVisible = isVisible
        binding.progressBar.isVisible = isVisible
        binding.downloadingAdditionalFormat.cardDownloadingInfo.isVisible = isVisible

        if (isVisible) {
            setupDownloadCallback(holder)
        }
    }

    private fun setupDownloadCallback(holder: DownloadingContentViewHolder) {
        val binding = holder.binding
        val context = holder.itemView.context
        var isExpanded = false
        val percentageCallback = { percentage: Float ->
            binding.downloadingTvProgress.text = context.getString(
                R.string.download_progress,
                percentage
            )
            binding.progressBar.progress = percentage.toInt()
        }
        val descriptionCallback = { description: String ->
            binding.downloadingAdditionalFormat.tvDownloadProgressInfo.text = description
        }
        downloadInfoVisibility(isExpanded, holder)
        downloadListener.updateVideoPercentage(percentageCallback, descriptionCallback)
        binding.downloadingAdditionalFormat.cardDownloadingInfo.setOnClickListener {
            isExpanded = !isExpanded
            downloadInfoVisibility(isExpanded, holder)
        }
    }

    private fun downloadInfoVisibility(isExpanded: Boolean, holder: DownloadingContentViewHolder) {
        val downloadInfo = holder.binding.downloadingAdditionalFormat
        val context = holder.itemView.context
        downloadInfo.tvDownloadProgressInfo.isVisible = isExpanded
        val image = if (isExpanded) R.drawable.icon_arrow_up else R.drawable.icon_arrow_down
        downloadInfo.btnExpand.setImageDrawable(
            AppCompatResources.getDrawable(context, image)
        )
    }

    private class DiffCallback : DiffUtil.ItemCallback<YtVideo>() {
        override fun areItemsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            return oldItem == newItem
        }
    }


}