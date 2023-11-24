package com.ayitinya.englishdictionary.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.history.History
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    settingsRepository: SettingsRepository,
    analytics: FirebaseAnalytics?
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryScreenUiState())
    val uiState: StateFlow<HistoryScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            launch {
                historyRepository.observeHistory().collect {
                    _uiState.value = HistoryScreenUiState(
                        historyList = it
                    )
                }
            }

            launch(Dispatchers.IO) {
                settingsRepository.readBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED).take(1)
                    .collect {
                        _uiState.update { uiState ->
                            uiState.copy(isHistoryActive = !it)
                        }
                    }
            }

            analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "HistoryScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HistoryScreen.kt")
            }
        }
    }

    fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(DefinitionScreenDestination(word = word), onlyIfResumed = true)
        }
    }

    fun toggleWordSelection(word: String) {
        _uiState.update { uiState ->
            uiState.copy(selectedHistory = uiState.selectedHistory.toMutableList().apply {
                if (contains(uiState.historyList.find { it.word == word })) {
                    remove(uiState.historyList.find { it.word == word })
                } else {
                    add(uiState.historyList.find { it.word == word }!!)
                }
            })
        }
    }

    suspend fun deleteSelectedHistoryItems() {
        historyRepository.deleteSelectedHistoryItems(_uiState.value.selectedHistory)
        _uiState.update { uiState ->
            uiState.copy(
                selectedHistory = emptyList<History>().toMutableList(),
                toastMessage = "Delete Successful"
            )
        }
    }

    fun selectAllHistoryItems() {
        _uiState.update { uiState ->
            uiState.copy(
                selectedHistory = uiState.historyList.toMutableList()
            )
        }
    }

    fun deselectAllHistoryItems() {
        _uiState.update { uiState ->
            uiState.copy(
                selectedHistory = emptyList<History>().toMutableList()
            )
        }
    }

    fun toastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun selectHistoryItem(word: String) {
        _uiState.update { uiState ->
            uiState.copy(selectedHistory = uiState.selectedHistory.toMutableList().apply {
                add(
                    uiState.historyList.find { it.word == word }!!
                )
            })
        }
        Log.i("f", _uiState.value.selectedHistory.toString())
    }
}