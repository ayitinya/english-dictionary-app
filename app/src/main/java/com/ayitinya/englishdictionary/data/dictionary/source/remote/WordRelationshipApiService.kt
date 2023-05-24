package com.ayitinya.englishdictionary.data.dictionary.source.remote

interface WordRelationshipApiService {
    suspend fun getRelationships(word: String): List<RemoteWordRelationship>
}