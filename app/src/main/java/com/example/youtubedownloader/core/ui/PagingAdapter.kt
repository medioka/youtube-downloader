package com.example.youtubedownloader.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.custom.ResultVideoListener
import com.example.youtubedownloader.databinding.ItemResultVideoBinding

class PagingAdapter(private val cardListener: ResultVideoListener) :
    PagingDataAdapter<YtVideo, PagingAdapter.PagingViewHolder>(UserComparator) {

    class PagingViewHolder(
        val binding: ItemResultVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context

        fun bind(video: YtVideo) {
            with(binding) {
                resultTvVideoTitle.text = video.title
                resultTvVideoDescription.text =
                    context.getString(R.string.description, video.artist, video.publishedDate)
                Glide.with(context).load(video.thumbnail)
                    .error(AppCompatResources.getDrawable(context, R.drawable.icon_error_photo))
                    .into(resultImgThumbnail)
            }
        }
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val video = getItem(position) ?: return
        val binding = holder.binding
        holder.bind(video)
        holder.itemView.setOnClickListener {
            cardListener.onClickCard(video)
        }
        binding.resultBtnFavorite.setOnClickListener {
            cardListener.onClickFavorite(video)
        }
        binding.resultBtnDownload.setOnClickListener {
            cardListener.onClickDownload(video)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val binding =
            ItemResultVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingViewHolder(binding)
    }



    private object UserComparator : DiffUtil.ItemCallback<YtVideo>() {
        override fun areItemsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            // Id is unique.
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            return oldItem == newItem
        }
    }
}