package com.ayitinya.englishdictionary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.WotdRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val wotdRepository: WotdRepository,
    private val dictionaryRepository: DictionaryRepository,
    analytics: FirebaseAnalytics?
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            wotdRepository.getWordOfTheDay().run {
                _uiState.update {
                    it.copy(
                        wotd = this,
                        isLoading = false,
                    )
                }
            }

            analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "HomeScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeScreen.kt")
            }
        }
    }

    fun getWordOfTheDay() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            wotdRepository.getWordOfTheDay().run {
                _uiState.update {
                    it.copy(
                        wotd = this,
                        isLoading = false,
                    )
                }
            }
        }
    }

    suspend fun getRandomWord() {
        withContext(Dispatchers.IO) {
            try {
                dictionaryRepository.getRandomWord().run {
                    _uiState.update {
                        it.copy(
                            randomWord = this,
                        )
                    }
                }
            } catch (e: Exception) {
                if (e.message == "No word found") {
                    _uiState.update {
                        it.copy(error = "No word found, database possibly not initialized, please try again")
                    }
                } else {
                    throw e
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun navigateToDefinitionScreen(
        word: String, fromWotd: Boolean? = false, navController: DestinationsNavigator
    ) {
        viewModelScope.launch {
            navController.navigate(
                DefinitionScreenDestination(word = word, fromWotd = fromWotd ?: false),
                onlyIfResumed = true
            )
        }
    }
}