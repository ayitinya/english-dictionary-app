package com.ayitinya.englishdictionary.data.settings

import android.content.Context
import com.ayitinya.englishdictionary.data.settings.source.local.readBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.readString
import com.ayitinya.englishdictionary.data.settings.source.local.readStringSync
import com.ayitinya.englishdictionary.data.settings.source.local.saveBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.saveString
import com.ayitinya.englishdictionary.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope
) : SettingsRepository {
    override suspend fun saveBoolean(key: String, value: Boolean) {
        applicationScope.launch(context = Dispatchers.IO) {
            context.saveBoolean(key, value)
        }
    }

    override suspend fun saveString(key: String, value: String) {
        applicationScope.launch {
            context.saveString(key, value)
        }
    }

    override suspend fun readStringSync(key: String): String? =
        context.readStringSync(key)

    override fun readString(key: String): Flow<String?> {
        return context.readString(key)
    }

    override fun readBoolean(key: String): Flow<Boolean> {
        return context.readBoolean(key)
    }
}