package com.ayitinya.englishdictionary.data.dictionary.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class RemoteDictionaryImpl @Inject constructor(private val client: HttpClient) : RemoteDictionary {
    override suspend fun getWordDetails(word: String): RemoteWordDefinition? {
        return try {
            val response = client.get {
                url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
            }.body<List<RemoteWordDefinition>>()
            response.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}