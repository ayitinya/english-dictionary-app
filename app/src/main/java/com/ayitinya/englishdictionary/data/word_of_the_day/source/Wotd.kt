package com.ayitinya.englishdictionary.data.word_of_the_day.source

import com.ayitinya.englishdictionary.data.word_of_the_day.source.local.LocalWotd
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WotdResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Wotd(
    val id: Int,
    val word: String,
    val pos: String,
    val sounds: String?,
    val glosses: String,
    val example: String?,
    val date: LocalDateTime
)

fun Wotd.toLocal(): LocalWotd = LocalWotd(
    id = id,
    word = word,
    pos = pos,
    sounds = sounds,
    glosses = glosses,
    example = example,
    date = date
)

fun LocalWotd.toExternal(): Wotd = Wotd(
    id = id,
    word = word,
    pos = pos,
    sounds = sounds,
    glosses = glosses,
    example = example,
    date = date
)

fun List<Wotd>.toLocal(): List<LocalWotd> = map { it.toLocal() }

fun List<LocalWotd>.toExternal(): List<Wotd> = map { it.toExternal() }

fun WotdResponse.toLocal(
    id: Int, pos: String, sounds: String?, glosses: String, example: String?
): LocalWotd = LocalWotd(
    id = id,
    word = wotd,
    pos = pos,
    sounds = sounds,
    glosses = glosses,
    example = example,
    date =LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)
