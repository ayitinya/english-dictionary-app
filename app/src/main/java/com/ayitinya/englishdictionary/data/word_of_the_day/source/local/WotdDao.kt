package com.ayitinya.englishdictionary.data.word_of_the_day.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WotdDao {
    @Query("SELECT * FROM WordOfTheDay")
    suspend fun getWordOfTheDay(): LocalWotd?

    @Query("SELECT * FROM WordOfTheDay")
    fun observeWordOfTheDay(): Flow<LocalWotd?>

    @Upsert
    suspend fun saveWordOfTheDay(localWotd: LocalWotd)
}