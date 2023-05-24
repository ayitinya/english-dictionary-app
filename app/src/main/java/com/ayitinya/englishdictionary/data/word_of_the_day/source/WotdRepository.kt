package com.ayitinya.englishdictionary.data.word_of_the_day.source

import kotlinx.coroutines.flow.Flow

interface WotdRepository {

    suspend fun updateWordOfTheDay()
    suspend fun getWordOfTheDay(): Wotd?

    suspend fun saveWordOfTheDay(word: String, timestamp: String)

    fun observeWordOfTheDay(): Flow<Wotd?>
}