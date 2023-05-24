package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Dictionary WHERE word LIKE :query GROUP BY word ORDER BY word COLLATE NOCASE ASC LIMIT 20")
    suspend fun search(query: String): List<LocalDictionary>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Dictionary NATURAL JOIN Senses WHERE word = :word")
    suspend fun getWordDetails(word: String): List<LocalDictionaryEntry>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Dictionary ORDER BY random() LIMIT 1")
    suspend fun getRandomWord(): LocalDictionary

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Dictionary NATURAL JOIN Senses WHERE word = :word")
    fun observeDictionaryEntry(word: String): Flow<List<LocalDictionaryEntry>>

//    Todo: check out pager library
}