package com.ayitinya.englishdictionary.data.dictionary

import com.ayitinya.englishdictionary.data.dictionary.source.local.DictionaryDao
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DictionaryRepositoryImpl @Inject constructor(
    private val localDataSource: DictionaryDao,
    private val favouritesRepository: FavouritesRepository
) : DictionaryRepository {
    @AddTrace(name = "searchDictionary")
    override suspend fun searchDictionary(query: String): List<Dictionary> {
        return withContext(Dispatchers.IO) {
            localDataSource.search("$query%").toExternal()
        }
    }

    @AddTrace(name = "getDictionaryEntries")
    override suspend fun getDictionaryEntries(query: String): DictionaryEntriesWithRelatedWords {
        return withContext(Dispatchers.IO) {
            val dictionaryEntries = localDataSource.getWordDetails(query).toExternal()
            return@withContext DictionaryEntriesWithRelatedWords(
                dictionaryEntries = dictionaryEntries,
            )
        }
    }

    override suspend fun getRandomWord(): Dictionary {
        return withContext(Dispatchers.IO) {
            localDataSource.getRandomWord().toExternal()
        }
    }

    override fun observeDictionaryEntry(word: String): Flow<DictionaryEntriesWithRelatedWords> {
        return localDataSource.observeDictionaryEntry(word).map {
            DictionaryEntriesWithRelatedWords(
                dictionaryEntries = it.toExternal(),
                isFavourite = favouritesRepository.isFavourite(word)
            )
        }
    }
}