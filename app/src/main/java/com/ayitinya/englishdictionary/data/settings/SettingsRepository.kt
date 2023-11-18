package com.ayitinya.englishdictionary.data.settings

import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveBoolean(key: SettingsKeys, value: Boolean)
    fun readBoolean(key: SettingsKeys): Flow<Boolean>
    suspend fun saveString(key: SettingsKeys, value: String)
    fun readString(key: SettingsKeys): Flow<String?>

    suspend fun readStringSync(key: SettingsKeys): String?
}