package com.example.youtubedownloader.core.data.remote.response

import com.example.youtubedownloader.core.data.remote.response.search.Item
import com.example.youtubedownloader.core.data.remote.response.search.PageInfo

data class SearchResponse(
    val etag: String?,
    val items: List<Item?>?,
    val kind: String?,
    val nextPageToken: String?,
    val pageInfo: PageInfo?,
    val regionCode: String?
)