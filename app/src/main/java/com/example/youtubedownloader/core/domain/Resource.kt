package com.example.youtubedownloader.core.domain

sealed class Resource<out T>(val data: T? = null, val exception: Exception? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    data object Loading : Resource<Nothing>()
    class Error<T>(exception: Exception, data: T? = null) : Resource<T>(data, exception)
}