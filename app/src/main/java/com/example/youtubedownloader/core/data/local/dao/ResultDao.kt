package com.example.youtubedownloader.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.youtubedownloader.core.data.local.entities.ResultItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ResultItem)

    @Delete
    suspend fun delete(item: ResultItem)

    @Update
    suspend fun update(item: ResultItem)

    @Query("SELECT * FROM result_item")
    fun getResultVideo(): Flow<List<ResultItem>>

    @Query("SELECT * FROM result_item WHERE isFavorite = 1")
    fun getFavoriteVideo(): Flow<List<ResultItem>>
}