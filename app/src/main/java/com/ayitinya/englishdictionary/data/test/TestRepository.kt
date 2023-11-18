package com.ayitinya.englishdictionary.data.test

interface TestRepository {
    suspend fun searchDictionary(query: String): List<Test>

    suspend fun getDictionaryEntries(query: String): TestEntry

}