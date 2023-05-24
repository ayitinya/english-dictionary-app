package com.ayitinya.englishdictionary.data.history.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "history")
data class LocalHistory(
    @PrimaryKey val word: String,
    val lastAccessed: LocalDateTime? = LocalDateTime.now()
)


