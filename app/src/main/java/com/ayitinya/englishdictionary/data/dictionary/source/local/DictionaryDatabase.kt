package com.ayitinya.englishdictionary.data.dictionary.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalSenses::class, LocalDictionary::class], version = 1, exportSchema = true
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}