package com.ayitinya.englishdictionary.data.word_of_the_day.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayitinya.englishdictionary.data.Converters

@Database(
    entities = [LocalWotd::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WotdDatabase: RoomDatabase() {
    abstract fun wotdDao(): WotdDao
}