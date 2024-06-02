package com.example.youtubedownloader.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.text.HtmlCompat
import com.example.youtubedownloader.core.data.local.entities.YtVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DataFormatter {
    private suspend fun convertUrlImageToUri(context: Context, video: YtVideo): String {
        return withContext(Dispatchers.IO) {
            val url = URL(video.thumbnail)
            val videoTitle = video.title
            //TODO check if file already exist
            try {
                val bitmapImage = BitmapFactory.decodeStream(url.openStream())
                val bytes = ByteArrayOutputStream()
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path =
                    MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        bitmapImage,
                        videoTitle,
                        null
                    )
                Uri.parse(path).toString()
            } catch (e: Exception) {
                ""
            }
        }
    }

     fun convertToStandardDateTime(date: String): String{
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val instant = Instant.parse(date)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        return localDateTime.format(formatter)
    }

    fun titleFormatter(title: String): String {
        return HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
    }

    fun convertUrlToYoutubeFormat(videoId: String): String {
        return "https://www.youtube.com/watch?v=$videoId"
    }



}