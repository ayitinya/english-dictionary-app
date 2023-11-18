package com.ayitinya.englishdictionary.data.dictionary

import com.ayitinya.englishdictionary.data.dictionary.source.local.LocalDictionary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

data class Dictionary(
    val id: Int,
    val word: String,
    val pos: String,
    val sound: String? = null,
    val etymology: String? = null,
    val senses: List<Sense> = emptyList()
)

@Serializable
data class Sense(
    @SerialName("raw_glosses") val glosses: List<String>, val examples: List<String>
)

@Serializable
private data class ParsedData(
    val etymology: String? = null,
    val sound: String? = null,
    @SerialName("pos") val partOfSpeech: String,
    val senses: List<Sense>
)

fun LocalDictionary.toExternal(): Dictionary {

    json.decodeFromString<ParsedData>(this.data).run {
        return Dictionary(
            id = id,
            word = word,
            pos = this.partOfSpeech,
            sound = this.sound,
            etymology = this.etymology,
            senses = this.senses
        )
    }
}

fun List<LocalDictionary>.toExternal(): List<Dictionary> {
    return map { it.toExternal() }
}
