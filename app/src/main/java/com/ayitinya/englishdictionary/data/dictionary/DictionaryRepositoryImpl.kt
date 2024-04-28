package com.ayitinya.englishdictionary.data.dictionary

import com.ayitinya.englishdictionary.data.dictionary.source.local.DictionaryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DictionaryRepositoryImpl @Inject constructor(
    private val localDataSource: DictionaryDao,
) : DictionaryRepository {
    override suspend fun searchDictionary(query: String): List<Dictionary> {
        return withContext(Dispatchers.IO) {
            localDataSource.search("$query%").toExternal()
        }
    }

    override suspend fun getDictionaryEntries(query: String): List<Dictionary> {
        return withContext(Dispatchers.IO) {
            localDataSource.getWordDetails(query).toExternal()
        }
    }

    override suspend fun getRandomWord(): Dictionary {
        return withContext(Dispatchers.IO) {
            localDataSource.getRandomWord().toExternal()
        }
    }

    override fun observeDictionaryEntry(word: String): Flow<List<Dictionary>> {
        return localDataSource.observeDictionaryEntry(word).map {
            it.toExternal()
        }
    }
}