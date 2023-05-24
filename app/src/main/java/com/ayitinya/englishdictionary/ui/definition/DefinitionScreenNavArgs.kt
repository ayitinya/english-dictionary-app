package com.ayitinya.englishdictionary.ui.definition

import android.os.Parcelable
import com.ayitinya.englishdictionary.data.dictionary.DictionaryEntriesWithRelatedWords
import kotlinx.parcelize.Parcelize

@Parcelize
data class DefinitionScreenNavArgs(
    val word: String,
    val entries: DictionaryEntriesWithRelatedWords,
) : Parcelable

// TODO: read parcelables and serializable
