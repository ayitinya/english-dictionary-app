package com.ayitinya.englishdictionary.data.dictionary.source.remote

import kotlinx.serialization.Serializable

@Serializable
data class RemoteWordRelationship(
    val relationshipType: String,
    val words: List<String> = emptyList()
)
