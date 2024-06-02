package com.example.youtubedownloader.core.data.local.converter

import androidx.room.TypeConverter
import com.example.youtubedownloader.core.domain.model.DownloadFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DownloadableFormatListConverter {
    @TypeConverter
    fun fromItemFormatList(value: List<DownloadFormat>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemFormatList(value: String): List<DownloadFormat> {
        return try {
            val listType = object : TypeToken<List<DownloadFormat>>() {}.type
            val personList: List<DownloadFormat> = Gson().fromJson(value, listType)
            personList
        } catch (e: Exception) {
            arrayListOf()
        }
    }

}
