package com.ayitinya.englishdictionary.ui.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private var analytics: FirebaseAnalytics = Firebase.analytics

    private val _uiState = MutableStateFlow(SearchScreenUiState())
    val uiState: MutableStateFlow<SearchScreenUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "SearchScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "SearchScreen.kt")
            }
            historyRepository.observeLastNumberHistory(5).collect { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }

    private val _queryTextChangedJob: MutableState<Job?> = mutableStateOf(null)

    suspend fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _queryTextChangedJob.value?.cancel()
        if (query.isEmpty()) {
            _queryTextChangedJob.value = null
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }
        _queryTextChangedJob.value = viewModelScope.launch(Dispatchers.Main) {
            delay(300L)
            searchDictionary(query)
        }
    }

    private suspend fun searchDictionary(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val searchResults = dictionaryRepository.searchDictionary(query.trim())
            _uiState.value = _uiState.value.copy(searchResults = searchResults, isLoading = false)
        }
    }

    suspend fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(DefinitionScreenDestination(word = word), onlyIfResumed = true)
        }
    }

}