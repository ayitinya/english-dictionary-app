package com.ayitinya.englishdictionary.data.dictionary.source.remote

import io.ktor.client.HttpClient
import javax.inject.Inject

class WordRelationshipApiServiceImpl @Inject constructor(private val client: HttpClient) :
    WordRelationshipApiService {
}