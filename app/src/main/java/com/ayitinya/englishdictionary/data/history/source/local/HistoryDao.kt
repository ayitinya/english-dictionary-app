package com.ayitinya.englishdictionary.data.history.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY lastAccessed DESC")
    fun observeHistory(): Flow<List<LocalHistory>>

    @Query("SELECT * FROM history ORDER BY lastAccessed DESC")
    suspend fun getHistory(): List<LocalHistory>

    @Query("DELETE FROM history WHERE word = :word")
    suspend fun deleteHistory(word: String)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()

    @Query("DELETE FROM history WHERE word IN (:words)")
    suspend fun deleteSelectedHistoryItems(words: List<String>)

    @Upsert
    suspend fun addHistory(localHistory: LocalHistory)

    @Query("SELECT EXISTS(SELECT * FROM history WHERE word = :word)")
    suspend fun checkHistoryExist(word: String): Boolean

    @Query("SELECT * FROM history ORDER BY lastAccessed DESC LIMIT :number")
    fun observeLastNumberHistory(number: Int): Flow<List<LocalHistory>>
}