package com.ayitinya.englishdictionary.data.word_of_the_day.source

import com.ayitinya.englishdictionary.data.dictionary.source.local.DictionaryDao
import com.ayitinya.englishdictionary.data.dictionary.toExternal
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.data.word_of_the_day.source.local.WotdDao
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WordOfTheDayApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWotdRepository @Inject constructor(
    private val dictionaryDataSource: DictionaryDao,
    private val localDataSource: WotdDao,
    private val remoteDataSource: WordOfTheDayApiService,
    private val settingsRepository: SettingsRepository
) : WotdRepository {

    override suspend fun updateWordOfTheDay() {
        withContext(Dispatchers.IO) {

            settingsRepository.readBoolean(SettingsKeys.IS_DATABASE_INITIALIZED).transformWhile {
                emit(it)
                !it
            }.collect {
                if (it) {
                    val wordOfTheDay = remoteDataSource.getWikiWotd()
                    if (wordOfTheDay != null) {
                        val wotd =
                            dictionaryDataSource.getWordDetails(wordOfTheDay.wotd).firstOrNull()
                        if (wotd != null) {
                            localDataSource.saveWordOfTheDay(wotd.toExternal().run {
                                wordOfTheDay.toLocal(
                                    id = 0,
                                    pos = this.pos,
                                    sound = this.sound,
                                    example = this.senses.firstOrNull()?.examples?.firstOrNull()
                                        ?: "",
                                    glosses = this.senses.firstOrNull()?.glosses?.firstOrNull()
                                        ?: ""
                                )
                            })
                        }
                    }
                }
            }
        }
    }

    override suspend fun getWordOfTheDay(): Wotd? {
        val wotd = localDataSource.getWordOfTheDay()?.toExternal()

        if (wotd == null) {
            return try {
                updateWordOfTheDay()
                localDataSource.getWordOfTheDay()?.toExternal()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
//           Todo This date comparison can lead to a bug in timezones that are ahead of UTC
            return if (wotd.date.dayOfYear == LocalDate.now().dayOfYear) {
                wotd
            } else {
                try {
                    updateWordOfTheDay()
                    localDataSource.getWordOfTheDay()?.toExternal()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    override suspend fun saveWordOfTheDay(word: String, timestamp: String) {
        TODO()
    }

    override fun observeWordOfTheDay(): Flow<Wotd?> {
        return localDataSource.observeWordOfTheDay().map { it?.toExternal() }
    }
}