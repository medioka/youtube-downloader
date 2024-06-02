package com.example.youtubedownloader.download.finished

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.custom.ClickAndShareListener
import com.example.youtubedownloader.databinding.ItemFinishedVideoBinding

class FinishedAdapter(
    private val itemListener: ClickAndShareListener
) :
    ListAdapter<YtVideo, FinishedAdapter.DownloadedViewHolder>(DiffCallback()) {


    class DownloadedViewHolder(val binding: ItemFinishedVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context
        fun bind(video: YtVideo) {
            with(binding) {
                finishedTvTitle.text = video.title
                finishedTvDescription.text =
                    context.getString(R.string.description, video.artist, video.publishedDate)
                finishedTvFileSize.text = video.downloadFormat?.fileSize
                finishedTvExtensionType.text = video.downloadFormat?.externalType
                Glide.with(context).load(video.thumbnail).into(finishedImgThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedViewHolder {
        val binding = ItemFinishedVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadedViewHolder, position: Int) {
        val video = currentList[position]
        holder.bind(video)
        holder.itemView.setOnClickListener {
            itemListener.onClickCard(video)
        }
        holder.binding.finishedBtnShare.setOnClickListener {
            itemListener.onShareClick(video.videoId)
        }
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
