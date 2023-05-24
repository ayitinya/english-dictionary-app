package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.ColumnInfo

data class LocalDictionaryEntry(
    @ColumnInfo(name = "word_id") val wordId: Int,
    val word: String,
    val pos: String,
    val sounds: String?,
    val glosses: String,
    val example: String?,
)
