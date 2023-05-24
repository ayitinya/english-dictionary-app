package com.ayitinya.englishdictionary.data.history

import com.ayitinya.englishdictionary.data.history.source.local.HistoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultHistoryRepository @Inject constructor(private val localDataSource: HistoryDao): HistoryRepository {
    override suspend fun getHistory(): List<History> {
        return localDataSource.getHistory().toExternal()
    }

    override suspend fun addHistory(word: String) {
        localDataSource.addHistory(History(word = word, lastAccessed = LocalDateTime.now()).toLocal())
    }

    override suspend fun deleteHistory(word: String) {
        localDataSource.deleteHistory(word)
    }

    override suspend fun deleteAllHistory() {
        localDataSource.deleteAllHistory()
    }

    override fun observeHistory(): Flow<List<History>> {
        return localDataSource.observeHistory().map { value -> value.toExternal() }
    }

    override fun observeLastNumberHistory(number: Int): Flow<List<History>> {
        return localDataSource.observeLastNumberHistory(number).map { value -> value.toExternal() }
    }
}