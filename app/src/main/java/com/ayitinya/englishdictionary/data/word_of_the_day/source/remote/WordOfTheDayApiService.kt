package com.ayitinya.englishdictionary.data.word_of_the_day.source.remote

interface WordOfTheDayApiService {
    suspend fun getWikiWotd(): WotdResponse?
}