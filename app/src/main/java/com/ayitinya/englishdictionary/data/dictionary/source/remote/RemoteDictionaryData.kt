package com.ayitinya.englishdictionary.data.dictionary.source.remote

import kotlinx.serialization.Serializable

@Serializable
data class RemoteWordRelationship(
    val relationshipType: String, val words: List<String> = emptyList()
)

@Serializable
data class Definition(
    val definition: String,
    val example: String? = null,
    val synonyms: List<String>?,
    val antonyms: List<String>?
)

@Serializable
data class Meaning(
    val partOfSpeech: String, val definitions: List<Definition>
)

@Serializable
data class Phonetic(
    val text: String? = null,
    val audio: String? = null
)


@Serializable
data class RemoteWordDefinition(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic>? = null,
    val origin: String? = null,
    val meanings: List<Meaning>,
)