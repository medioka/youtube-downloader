package com.example.youtubedownloader.core.domain.model

sealed class Container {
    data class Audio(val container: AudioContainer) : Container()
    data class Video(val container: VideoContainer) : Container()
    data class Default(val container: DefaultContainer) : Container()
}

enum class VideoContainer {
    MKV, WEBM, MP4
}

enum class AudioContainer {
    M4A, AAC, MP3
}

enum class DefaultContainer { DEFAULT }




