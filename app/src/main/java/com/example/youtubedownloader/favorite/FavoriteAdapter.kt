package com.example.youtubedownloader.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import com.example.youtubedownloader.custom.ResultVideoListener
import com.example.youtubedownloader.databinding.ItemResultVideoBinding

class FavoriteAdapter(
    private val cardListener: ResultVideoListener
) : ListAdapter<YtVideo, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffCallback) {

    var originalList = listOf<YtVideo>()


    class FavoriteViewHolder(
        val binding: ItemResultVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context
        fun bind(video: YtVideo) {
            with(binding) {
                resultTvVideoTitle.text = video.title
                resultTvVideoDescription.text =
                    context.getString(R.string.description, video.artist, video.publishedDate)
                Glide.with(context)
                    .load(video.thumbnail)
                    .into(resultImgThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding =
            ItemResultVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val video = currentList[position]
        holder.bind(video)
        holder.itemView.setOnClickListener {
            cardListener.onClickCard(video)
        }
        holder.binding.resultBtnFavorite.setOnClickListener {
            cardListener.onClickFavorite(video)
        }
        holder.binding.resultBtnDownload.setOnClickListener {
            cardListener.onClickDownload(video)
        }
    }

    fun modifyList(videoList: List<YtVideo>?) {
        videoList?.let {
            originalList = videoList
            submitList(videoList)
        }
    }

    fun filter(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            submitList(originalList)
            return
        }
        val videoList = originalList.filter {
            it.title.lowercase().contains(searchQuery.lowercase())
        }
        submitList(videoList)
    }

    private object FavoriteDiffCallback : DiffUtil.ItemCallback<YtVideo>() {
        override fun areItemsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YtVideo, newItem: YtVideo): Boolean {
            return oldItem == newItem
        }
    }

}