package com.ayitinya.englishdictionary.data.dictionary

import android.os.Parcelable
import com.ayitinya.englishdictionary.data.dictionary.source.local.LocalDictionaryEntry
import com.ayitinya.englishdictionary.data.dictionary.source.remote.RemoteWordRelationship
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

fun LocalDictionaryEntry.toExternal(): DictionaryEntry {
    return DictionaryEntry(
        wordId = wordId,
        word = word,
        pos = pos,
        sounds = sounds,
        glosses = glosses,
        example = example,
        etymology = etymology
    )
}

@JvmName("toExternalLocalDictionaryEntry")
fun List<LocalDictionaryEntry>.toExternal(): List<DictionaryEntry> {
    return map { it.toExternal() }
}

fun RemoteWordRelationship.toExternal(): RelatedWords {
    return RelatedWords(
        relationshipType = relationshipType,
        words = words
    )
}

@JvmName("toExternalRemoteWordRelationship")
fun List<RemoteWordRelationship>.toExternal(): List<RelatedWords> {
    return map { it.toExternal() }
}