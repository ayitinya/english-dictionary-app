package com.ayitinya.englishdictionary.data.test

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DictionaryZstd::class, ZstdDicts::class],
    views = [LocalTest::class],
    version = 3,
    exportSchema = true,
    autoMigrations = []
)
abstract class TestDatabase : RoomDatabase() {
    abstract fun testDao(): TestDao
}