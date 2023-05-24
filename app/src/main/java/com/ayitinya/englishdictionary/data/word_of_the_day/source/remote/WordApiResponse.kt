package com.ayitinya.englishdictionary.data.word_of_the_day.source.remote

import kotlinx.serialization.Serializable


@Serializable
data class WordApiResponse(
    val word: String,
    val pdd: String,
    val definitions: List<Definition> = emptyList(),
    val examples: List<Example> = emptyList(),
)

@Serializable
data class Definition(
    val text: String,
    val partOfSpeech: String
)

@Serializable
data class Example(
    val text: String
)

@Serializable
data class WotdResponse(val wotd: String, val timestamp: String)