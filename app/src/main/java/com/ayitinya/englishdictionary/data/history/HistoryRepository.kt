package com.ayitinya.englishdictionary.data.history

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun getHistory(): List<History>

    suspend fun addHistory(word: String)

    suspend fun deleteHistory(word: String)

    suspend fun deleteSelectedHistoryItems(words: List<History>)

    suspend fun deleteAllHistory()

    fun observeHistory(): Flow<List<History>>

    fun observeLastNumberHistory(number: Int): Flow<List<History>>

    suspend fun clearHistory()
}