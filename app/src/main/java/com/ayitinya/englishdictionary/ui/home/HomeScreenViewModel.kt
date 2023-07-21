package com.ayitinya.englishdictionary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.WotdRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
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
    analytics: FirebaseAnalytics
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(wotd = wotdRepository.getWordOfTheDay(), isLoading = false)
            }
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "HomeScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeScreen.kt")
            }
        }
    }

    suspend fun getRandomWord() {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(randomWord = dictionaryRepository.getRandomWord())
            }
        }
    }

    fun navigateToDefinitionScreen(
        word: String,
        fromWotd: Boolean? = false,
        navController: DestinationsNavigator
    ) {
        viewModelScope.launch {
            navController.navigate(
                DefinitionScreenDestination(word = word, fromWotd = fromWotd ?: false),
                onlyIfResumed = true
            )
        }
    }
}