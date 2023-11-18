package com.ayitinya.englishdictionary.data

import androidx.room.TypeConverter
import com.ayitinya.englishdictionary.data.dictionary.Sense
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun senseToString(sense: Sense?): String? {
        return Json.encodeToString(sense)
    }

    @TypeConverter
    fun stringToSense(value: String?): Sense? {
        return value?.let { Json.decodeFromString<Sense>(it) }
    }
}