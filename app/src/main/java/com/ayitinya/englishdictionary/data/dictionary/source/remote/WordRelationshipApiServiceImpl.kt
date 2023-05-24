package com.ayitinya.englishdictionary.data.dictionary.source.remote

import android.util.Log
import com.ayitinya.englishdictionary.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class WordRelationshipApiServiceImpl @Inject constructor(private val client: HttpClient) :
    WordRelationshipApiService {
    override suspend fun getRelationships(word: String): List<RemoteWordRelationship> {
        return try {
            client.get("https://api.wordnik.com/v4/word.json/${word}/relatedWords?useCanonical=false&limitPerRelationshipType=10&api_key=${BuildConfig.WORDNIK_API}")
                .body()
        } catch (e: Exception) {
            Log.d("WordRelationshipApi", "getRelationships: $e")
            emptyList()
        }
    }
}