package com.ayitinya.englishdictionary.data.history

import com.ayitinya.englishdictionary.data.history.source.local.LocalHistory
import java.time.LocalDateTime

data class History(
    val word: String,
    val lastAccessed: LocalDateTime? = LocalDateTime.now()
)

fun LocalHistory.toExternal(): History {
    return History(
        word = word,
        lastAccessed = lastAccessed
    )
}

fun List<LocalHistory>.toExternal(): List<History> = map { it.toExternal() }

fun History.toLocal(): LocalHistory {
    return LocalHistory(
        word = word,
        lastAccessed = lastAccessed
    )
}

fun List<History>.toLocal(): List<LocalHistory> = map { it.toLocal() }
