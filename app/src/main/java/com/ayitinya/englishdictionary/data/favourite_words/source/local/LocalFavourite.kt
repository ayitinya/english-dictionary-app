package com.ayitinya.englishdictionary.data.favourite_words.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favourites")
data class LocalFavourite(@PrimaryKey(autoGenerate = true) val id: Int?, val word: String)
