package com.example.youtubedownloader.core.data.remote.response.search

data class Snippet(
    val channelId: String?,
    val channelTitle: String?,
    val description: String?,
    val liveBroadcastContent: String?,
    val publishTime: String?,
    val publishedAt: String?,
    val thumbnails: Thumbnails?,
    val title: String?
)