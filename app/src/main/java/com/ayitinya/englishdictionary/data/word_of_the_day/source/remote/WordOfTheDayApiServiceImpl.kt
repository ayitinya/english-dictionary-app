package com.ayitinya.englishdictionary.data.word_of_the_day.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordOfTheDayApiServiceImpl @Inject constructor(private val client: HttpClient) :
    WordOfTheDayApiService {
    override suspend fun getWikiWotd(): WotdResponse? {
        return try {
            client.get {
                url("https://wotd.ayitinya.me/api/main")
            }.body()
        } catch (e: Exception) {
            null
        }
    }
}