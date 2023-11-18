package com.ayitinya.englishdictionary.data.word_of_the_day.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.ayitinya.englishdictionary.data.Converters

@Database(
    entities = [LocalWotd::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(
        from = 1, to = 2, spec = WotdDatabase.AutoMigrationFrom1To2::class
    )]
)
@TypeConverters(Converters::class)
abstract class WotdDatabase : RoomDatabase() {
    abstract fun wotdDao(): WotdDao

    @RenameColumn.Entries(
        RenameColumn(
            tableName = "WordOfTheDay", fromColumnName = "sounds", toColumnName = "sound"
        )
    )
    @DeleteColumn.Entries(
        DeleteColumn(tableName = "WordOfTheDay", columnName = "glosses"),
        DeleteColumn(tableName = "WordOfTheDay", columnName = "example")
    )
    class AutoMigrationFrom1To2 : AutoMigrationSpec
}