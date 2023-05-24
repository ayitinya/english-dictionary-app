package com.ayitinya.englishdictionary.data.favourite_words

import com.ayitinya.englishdictionary.data.favourite_words.source.local.FavouritesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFavouritesRepository @Inject constructor(private val localDataSource: FavouritesDao) :
    FavouritesRepository {
    override fun getFavourites(): Flow<List<Favourite>> {
        return localDataSource.getFavouriteWords().map { value -> value.toExternal() }
    }

    override suspend fun insertFavourite(word: String) {
        withContext(Dispatchers.IO) {
            localDataSource.insertFavouriteWord(Favourite(word = word).toLocal())
        }
    }

    override suspend fun removeFavourite(word: String) {
        withContext(Dispatchers.IO) {
            localDataSource.removeFavouriteWord(Favourite(word = word).toLocal())
        }
    }

    override suspend fun isFavourite(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            localDataSource.isFavourite(word)
        }
    }
}