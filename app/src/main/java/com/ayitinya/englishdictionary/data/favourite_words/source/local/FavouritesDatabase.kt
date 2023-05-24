package com.ayitinya.englishdictionary.data.favourite_words.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalFavourite::class],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun favouritesDao(): FavouritesDao
}