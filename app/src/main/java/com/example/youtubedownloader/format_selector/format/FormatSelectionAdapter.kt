package com.example.youtubedownloader.format_selector.format

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.example.youtubedownloader.custom.FormatClickListener
import com.example.youtubedownloader.databinding.ItemDownloadFormatBinding

class FormatSelectionAdapter(private val onItemClick: FormatClickListener) :
    ListAdapter<DownloadFormat, FormatSelectionAdapter.FormatViewHolder>(DiffCallback()) {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var lastSelectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormatViewHolder {
        val binding =
            ItemDownloadFormatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FormatViewHolder(binding)
    }


    class FormatViewHolder(val binding: ItemDownloadFormatBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onBindViewHolder(holder: FormatViewHolder, position: Int) {
        val currentItem = currentList[position]
        val context = holder.itemView.context
        with(holder.binding) {
            tvDownloadVideoFormat.text = context.getString(
                R.string.description_download_format,
                currentItem.formatNote,
                currentItem.externalType
            )
            tvFileSize.text = currentItem.fileSize
            setupCardView(holder, position)
        }
    }

    private fun setupCardView(holder: FormatViewHolder, position: Int) {
        val binding = holder.binding
        val context = holder.itemView.context

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(context.getColor(android.R.color.holo_red_light))
        } else {
            holder.itemView.setBackgroundColor(context.getColor(android.R.color.transparent))
        }

        binding.card.setOnClickListener {
            selectedPosition = position
            if (lastSelectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(lastSelectedPosition)
            }
            lastSelectedPosition = selectedPosition
            notifyItemChanged(selectedPosition)
            onItemClick.onFormatClickListener(currentList[position])
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<DownloadFormat>() {

    override fun areItemsTheSame(oldItem: DownloadFormat, newItem: DownloadFormat): Boolean {
        return oldItem.formatId == newItem.formatId
    }

    override fun areContentsTheSame(oldItem: DownloadFormat, newItem: DownloadFormat): Boolean {
        return oldItem == newItem
    }
}