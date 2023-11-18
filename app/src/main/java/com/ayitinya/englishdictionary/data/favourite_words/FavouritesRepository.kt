package com.ayitinya.englishdictionary.data.favourite_words

import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    fun getFavourites(): Flow<List<Favourite>>

    suspend fun insertFavourite(word: String)

    suspend fun removeFavourite(word: String)

    suspend fun isFavourite(word: String): Boolean

    suspend fun clearFavorites()

    suspend fun deleteSelectedFavoriteItems(selectedFavourites: List<Favourite>)
}