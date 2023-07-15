package com.ayitinya.englishdictionary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.WotdRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
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
) : ViewModel() {
    private var analytics: FirebaseAnalytics = Firebase.analytics

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "HomeScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeScreen.kt")
            }
            _uiState.update {
                it.copy(wotd = wotdRepository.getWordOfTheDay(), isLoading = false)
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

    suspend fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(
                DefinitionScreenDestination(word = word), onlyIfResumed = true
            )
        }
    }
}