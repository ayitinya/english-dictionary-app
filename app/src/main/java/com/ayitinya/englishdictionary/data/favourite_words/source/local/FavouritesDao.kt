package com.ayitinya.englishdictionary.data.favourite_words.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Favourites ORDER BY id DESC")
    fun getFavouriteWords(): Flow<List<LocalFavourite>>

    @Upsert
    suspend fun insertFavouriteWord(localFavourite: LocalFavourite)

    @Delete
    suspend fun removeFavouriteWord(localFavourite: LocalFavourite)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT EXISTS(SELECT * FROM Favourites WHERE word = :word)")
    suspend fun isFavourite(word: String): Boolean

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Favourites WHERE word = :word")
    suspend fun getFavouriteWord(word: String): LocalFavourite

    @Query("DELETE FROM Favourites")
    suspend fun deleteAll()

    @Query("DELETE FROM Favourites WHERE word IN (:selectedFavourites)")
    suspend fun deleteSelectedFavouriteItems(selectedFavourites: List<String>)
}