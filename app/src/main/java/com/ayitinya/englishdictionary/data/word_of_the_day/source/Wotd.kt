package com.ayitinya.englishdictionary.data.word_of_the_day.source

import com.ayitinya.englishdictionary.data.dictionary.Sense
import com.ayitinya.englishdictionary.data.word_of_the_day.source.local.LocalWotd
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WotdResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Wotd(
    val id: Int,
    val word: String,
    val pos: String,
    val sound: String?,
    val sense: Sense?,
    val date: LocalDateTime
)

fun Wotd.toLocal(): LocalWotd = LocalWotd(
    id = id,
    word = word,
    pos = pos,
    sound = sound,
    sense = sense,
    date = date
)

fun LocalWotd.toExternal(): Wotd = Wotd(
    id = id,
    word = word,
    pos = pos,
    sound = sound,
    sense = sense,
    date = date
)

fun List<Wotd>.toLocal(): List<LocalWotd> = map { it.toLocal() }

fun List<LocalWotd>.toExternal(): List<Wotd> = map { it.toExternal() }

fun WotdResponse.toLocal(
    id: Int, pos: String, sound: String?, glosses: String, example: String?
): LocalWotd = LocalWotd(
    id = id,
    word = wotd,
    pos = pos,
    sound = sound,
    sense = Sense(
        glosses = listOf(glosses),
        examples = listOf(if (example.isNullOrBlank()) "" else example)
    ),
    date = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)
