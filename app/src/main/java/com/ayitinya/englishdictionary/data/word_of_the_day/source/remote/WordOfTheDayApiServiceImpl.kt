package com.ayitinya.englishdictionary.data.word_of_the_day.source.remote

import android.util.Log
import com.ayitinya.englishdictionary.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordOfTheDayApiServiceImpl @Inject constructor(private val client: HttpClient) :
    WordOfTheDayApiService {
    override suspend fun getWordOfTheDay(): WordApiResponse? {

        return try {
            client.get {
                url("https://api.wordnik.com/v4/words.json/wordOfTheDay?api_key=${BuildConfig.WORDNIK_API}")
            }.body()
        } catch (e: Exception) {
            Log.d("WordOfTheDayApiService", "getWordOfTheDay: $e")
            return null
        }
    }

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