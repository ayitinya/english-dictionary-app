package com.ayitinya.englishdictionary.data.word_of_the_day.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "WordOfTheDay")
data class LocalWotd(
    @PrimaryKey val id: Int,
    val word: String,
    val pos: String,
    val sounds: String?,
    val glosses: String,
    val example: String?,
    val date: LocalDateTime
)
