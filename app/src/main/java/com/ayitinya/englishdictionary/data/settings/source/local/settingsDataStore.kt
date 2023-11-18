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

suspend fun Context.saveBoolean(key: SettingsKeys, value: Boolean) {
    dataStore.edit { settings ->
        settings[booleanPreferencesKey(key.value)] = value
    }
}

fun Context.readBoolean(key: SettingsKeys): Flow<Boolean> =
    dataStore.data.map { settings -> settings[booleanPreferencesKey(key.value)] ?: false }

suspend fun Context.saveString(key: SettingsKeys, value: String) {
    dataStore.edit { settings ->
        settings[stringPreferencesKey(key.value)] = value
    }
}

suspend fun Context.readStringSync(key: SettingsKeys): String? =
    dataStore.data.first()[stringPreferencesKey(key.value)]

fun Context.readString(key: SettingsKeys): Flow<String?> =
    dataStore.data.map { settings -> settings[stringPreferencesKey(key.value)] }