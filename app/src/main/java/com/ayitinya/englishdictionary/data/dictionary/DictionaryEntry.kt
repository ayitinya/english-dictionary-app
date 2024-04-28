package com.ayitinya.englishdictionary.data.dictionary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RelatedWords(
    val relationshipType: String,
    val words: List<String>
) : Parcelable

@Parcelize
data class DictionaryEntry(
    val wordId: Int,
    val word: String,
    val pos: String,
    val sounds: String?,
    val glosses: String,
    val example: String?,
    val etymology: String?
) : Parcelable

@Parcelize
data class DictionaryEntriesWithRelatedWords(
    val dictionaryEntries: List<DictionaryEntry> = emptyList(),
    val relatedWords: List<RelatedWords> = emptyList(),
    val isFavourite: Boolean = false,
) : Parcelable
