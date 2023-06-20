package com.ayitinya.englishdictionary.data.dictionary.source.remote

interface RemoteDictionary {
    suspend fun getWordDetails(word: String): RemoteWordDefinition?
}