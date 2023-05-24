package com.ayitinya.englishdictionary.data.dictionary

import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun searchDictionary(query: String): List<Dictionary>

    suspend fun getDictionaryEntries(query: String): DictionaryEntriesWithRelatedWords

    suspend fun getRandomWord(): Dictionary

    fun observeDictionaryEntry(word: String): Flow<DictionaryEntriesWithRelatedWords>
}