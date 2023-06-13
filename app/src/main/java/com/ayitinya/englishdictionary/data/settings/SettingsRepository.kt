package com.ayitinya.englishdictionary.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveBoolean(key: String, value: Boolean)
    fun readBoolean(key: String): Flow<Boolean>
    suspend fun saveString(key: String, value: String)
    fun readString(key: String): Flow<String?>

    suspend fun readStringSync(key: String): String?
}