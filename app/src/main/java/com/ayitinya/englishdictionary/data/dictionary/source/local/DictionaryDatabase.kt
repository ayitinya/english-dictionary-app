package com.ayitinya.englishdictionary.data.dictionary.source.local

import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [DictionaryZstd::class, ZstdDicts::class],
    views = [LocalDictionary::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = DictionaryDatabase.AutoMigration3To4::class)
    ]
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao

    @DeleteTable.Entries(DeleteTable("Senses"), DeleteTable("Dictionary"))
    class AutoMigration3To4 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            Log.d("Migrate", "Migrated")
        }
    }
}