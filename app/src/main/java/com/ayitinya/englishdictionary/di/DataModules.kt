@file:Suppress("unused", "unused", "unused", "unused", "unused")

package com.ayitinya.englishdictionary.di

import android.content.Context
import androidx.room.Room
import com.ayitinya.englishdictionary.data.dictionary.DefaultDictionaryRepository
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.dictionary.source.local.DictionaryDatabase
import com.ayitinya.englishdictionary.data.dictionary.source.remote.WordRelationshipApiService
import com.ayitinya.englishdictionary.data.dictionary.source.remote.WordRelationshipApiServiceImpl
import com.ayitinya.englishdictionary.data.favourite_words.DefaultFavouritesRepository
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.data.favourite_words.source.local.FavouritesDatabase
import com.ayitinya.englishdictionary.data.history.DefaultHistoryRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.data.history.source.local.HistoryDatabase
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.WotdRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.local.WotdDatabase
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WordOfTheDayApiService
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WordOfTheDayApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindDictionaryRepository(repository: DefaultDictionaryRepository): DictionaryRepository

    @Singleton
    @Binds
    abstract fun bindFavouritesRepository(repository: DefaultFavouritesRepository): FavouritesRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(repository: DefaultHistoryRepository): HistoryRepository

    @Singleton
    @Binds
    abstract fun bindWotdRepository(repository: DefaultWotdRepository): WotdRepository

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDictionaryDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        return Room.databaseBuilder(
            context.applicationContext, DictionaryDatabase::class.java, "dictionary.db"
        ).createFromAsset("database/data.db").fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDictionaryDao(database: DictionaryDatabase) = database.dictionaryDao()

    @Singleton
    @Provides
    fun provideFavouritesDatabase(@ApplicationContext context: Context): FavouritesDatabase {
        return Room.databaseBuilder(
            context.applicationContext, FavouritesDatabase::class.java, "favourites.db"
        ).build()
    }

    @Provides
    fun provideFavouritesDao(database: FavouritesDatabase) = database.favouritesDao()

    @Singleton
    @Provides
    fun provideHistoryDatabase(@ApplicationContext context: Context): HistoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext, HistoryDatabase::class.java, "history.db"
        ).build()
    }

    @Provides
    fun provideHistoryDao(database: HistoryDatabase) = database.historyDao()

    @Singleton
    @Provides
    fun provideWotdDatabase(@ApplicationContext context: Context): WotdDatabase {
        return Room.databaseBuilder(
            context.applicationContext, WotdDatabase::class.java, "wotd.db"
        ).build()
    }

    @Provides
    fun provideWotdDao(database: WotdDatabase) = database.wotdDao()

}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }

        install(HttpCache) {
            val cacheFile = File.createTempFile("ktor_cache", ".tmp")
            privateStorage(FileStorage(cacheFile))
        }
    }

}

@Suppress("unused", "unused", "unused", "unused", "unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServicesModule {

    @Singleton
    @Binds
    abstract fun bindWordOfTheDayApiService(service: WordOfTheDayApiServiceImpl): WordOfTheDayApiService

    @Singleton
    @Binds
    abstract fun bindWordRelationshipApiService(service: WordRelationshipApiServiceImpl): WordRelationshipApiService

}
