package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Senses", foreignKeys = [ForeignKey(
        entity = LocalDictionary::class,
        parentColumns = arrayOf("word_id"),
        childColumns = arrayOf("word_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION
    )],
)
data class LocalSenses(
    @ColumnInfo(name = "sense_id") @PrimaryKey(autoGenerate = true) val senseId: Int,
    @ColumnInfo(name = "word_id", index = true) val wordId: Int,
    @ColumnInfo(name = "glosses") val glosses: String,
    @ColumnInfo(name = "example") val example: String?,
)
