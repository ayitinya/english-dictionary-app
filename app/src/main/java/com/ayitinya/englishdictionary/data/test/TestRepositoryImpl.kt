package com.ayitinya.englishdictionary.data.test

import android.util.Log
import com.ayitinya.englishdictionary.data.favourite_words.source.local.FavouritesDao
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Serializable
data class ParsedSense(
    @SerialName("raw_glosses") val glosses: List<String>,
    val examples: List<String>
)

@Serializable
data class Test(
    val etymology: String? = null,
    val sound: String? = null,
    @SerialName("pos") val partOfSpeech: String,
    val senses: List<ParsedSense>
)

data class TestEntry(
    val data: List<Test> = emptyList(),
    val word: String
)

private var json = Json {
    ignoreUnknownKeys = true
}

@Singleton
class TestRepositoryImpl @Inject constructor(
    private val localDataSource: TestDao,
    private val favouritesDataSource: FavouritesDao
) : TestRepository {
    @AddTrace(name = "searchDictionary")
    override suspend fun searchDictionary(query: String): List<Test> {
        return withContext(Dispatchers.IO) {
            localDataSource.search("$query%").map {
                Log.d("HER", it.data)
                json.decodeFromString<Test>(it.data)
            }
        }
    }

    @AddTrace(name = "getDictionaryEntry")
    override suspend fun getDictionaryEntries(query: String): TestEntry {
        val d = withContext(Dispatchers.IO) {
            localDataSource.getWordDetails(query).run {
                return@withContext TestEntry(
                    data = map {
                        json.decodeFromString<Test>(it.data)
                    },
                    word = query
                )
            }
        }
        Log.d("EI", d.data.toString())
        return d
    }
}