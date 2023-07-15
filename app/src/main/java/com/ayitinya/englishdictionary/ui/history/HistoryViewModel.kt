package com.ayitinya.englishdictionary.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private var analytics: FirebaseAnalytics = Firebase.analytics

    private val _uiState = MutableStateFlow(HistoryScreenUiState())
    val uiState: MutableStateFlow<HistoryScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "HistoryScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HistoryScreen.kt")
            }
            historyRepository.observeHistory().collect {
                _uiState.value = HistoryScreenUiState(
                    historyList = it
                )
            }
        }
    }

    fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(DefinitionScreenDestination(word = word), onlyIfResumed = true)
        }
    }

    fun selectHistoryItem(word: String) {
        _uiState.update { uiState ->
            uiState.copy(
                selectedHistory = uiState.selectedHistory.toMutableList().apply {
                    add(
                        uiState.historyList.find { it.word == word }!!
                    )
                }
            )
        }
        Log.i("f", _uiState.value.selectedHistory.toString())
    }

    fun removeHistoryItems() {
//        don't use viewmodel scope here, because it will be cleared when the viewmodel is cleared
        viewModelScope.launch {
//            historyRepository.removeAll()
        }
    }


}