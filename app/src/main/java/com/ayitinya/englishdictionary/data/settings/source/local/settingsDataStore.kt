package com.ayitinya.englishdictionary.data.settings.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

const val PREFERENCES_NAME = "settings_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

suspend fun Context.saveBoolean(key: String, value: Boolean) {
    dataStore.edit { settings ->
        settings[booleanPreferencesKey(key)] = value
    }
}

fun Context.readBoolean(key: String): Flow<Boolean> =
    dataStore.data.map { settings -> settings[booleanPreferencesKey(key)] ?: false }

suspend fun Context.saveString(key: String, value: String) {
    dataStore.edit { settings ->
        settings[stringPreferencesKey(key)] = value
    }
}

suspend fun Context.readStringSync(key: String): String? =
    dataStore.data.first()[stringPreferencesKey(key)]

fun Context.readString(key: String): Flow<String?> =
    dataStore.data.map { settings -> settings[stringPreferencesKey(key)] }