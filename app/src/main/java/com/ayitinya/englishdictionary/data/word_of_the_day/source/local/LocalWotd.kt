package com.ayitinya.englishdictionary.data.word_of_the_day.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ayitinya.englishdictionary.data.dictionary.Sense
import java.time.LocalDateTime

@Entity(tableName = "WordOfTheDay")
data class LocalWotd(
    @PrimaryKey val id: Int,
    val word: String,
    val pos: String,
    val sound: String?,
    val sense: Sense?,
    val date: LocalDateTime
)
