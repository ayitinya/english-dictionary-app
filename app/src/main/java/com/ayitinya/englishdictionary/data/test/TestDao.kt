package com.ayitinya.englishdictionary.data.test

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.SkipQueryVerification
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.flow.Flow


@Dao
interface TestDao {
    @AddTrace(name = "search")
    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query(
        "SELECT * FROM LocalTest " +
                "WHERE word " +
                "LIKE :query GROUP BY word " +
                "ORDER BY word " +
                "COLLATE NOCASE ASC " +
                "LIMIT 20"
    )
    suspend fun search(query: String): List<LocalTest>

    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalTest WHERE word = :word")
    suspend fun getWordDetails(word: String): List<LocalTest>

    @AddTrace(name = "getRandomWord")
    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalTest ORDER BY random() LIMIT 1")
    suspend fun getRandomWord(): LocalTest

    @RewriteQueriesToDropUnusedColumns
    @SkipQueryVerification
    @Query("SELECT * FROM LocalTest WHERE word = :word")
    fun observeDictionaryEntry(word: String): Flow<LocalTest>

//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM Dictionary NATURAL JOIN Senses WHERE word = :word")
//    suspend fun getWordDetails(word: String): List<LocalDictionaryEntry>
//
//    @AddTrace(name = "getRandomWord")
//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM Dictionary ORDER BY random() LIMIT 1")
//    suspend fun getRandomWord(): LocalTest
//
//    @RewriteQueriesToDropUnusedColumns
//    @Query("SELECT * FROM Dictionary NATURAL JOIN Senses WHERE word = :word")
//    fun observeDictionaryEntry(word: String): Flow<List<LocalDictionaryEntry>>

//    Todo: check out pager library
}