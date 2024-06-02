package com.example.youtubedownloader.core.domain.model

data class DownloadOption(
    var container: String = Container.Default(DefaultContainer.DEFAULT).container.name
)