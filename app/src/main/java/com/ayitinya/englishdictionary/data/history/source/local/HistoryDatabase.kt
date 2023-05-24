package com.ayitinya.englishdictionary.data.history.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayitinya.englishdictionary.data.Converters

@TypeConverters(Converters::class)
@Database(
    entities = [LocalHistory::class], version = 1, exportSchema = true, autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}