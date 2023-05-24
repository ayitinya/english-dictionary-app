package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Dictionary")
data class LocalDictionary(
    @ColumnInfo(name = "word_id") @PrimaryKey(autoGenerate = true) val wordId: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "pos") val pos: String,
    @ColumnInfo(name = "sounds") val sounds: String?,
)
