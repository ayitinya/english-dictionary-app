package com.ayitinya.englishdictionary.data.dictionary

import com.ayitinya.englishdictionary.data.dictionary.source.local.LocalDictionary

data class Dictionary(
    val wordId: Int,
    val word: String,
    val pos: String,
    val sounds: String?,
    val etymology: String?
)

fun LocalDictionary.toExternal(): Dictionary {
    return Dictionary(
        wordId = wordId,
        word = word,
        pos = pos,
        sounds = sounds,
        etymology = etymology
    )
}

fun List<LocalDictionary>.toExternal(): List<Dictionary> {
    return map { it.toExternal() }
}
