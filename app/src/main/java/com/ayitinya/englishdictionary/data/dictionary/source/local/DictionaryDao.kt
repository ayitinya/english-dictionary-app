package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.SkipQueryVerification
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @AddTrace(name = "search")
    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query(
        "SELECT * FROM LocalDictionary " +
                "WHERE word LIKE :query " +
                "GROUP BY word ORDER BY word " +
                "COLLATE NOCASE ASC LIMIT 20"
    )
    suspend fun search(query: String): List<LocalDictionary>

    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalDictionary WHERE word = :word")
    suspend fun getWordDetails(word: String): List<LocalDictionary>

    @AddTrace(name = "getRandomWord")
    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalDictionary ORDER BY random() LIMIT 1")
    suspend fun getRandomWord(): LocalDictionary

    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalDictionary WHERE word = :word")
    fun observeDictionaryEntry(word: String): Flow<List<LocalDictionary>>

//    Todo: check out pager library
}