package com.ayitinya.englishdictionary.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.domain.ActivateWotdNotificationUseCase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val favouriteRepository: FavouritesRepository,
    private val activateWotdNotificationUseCase: ActivateWotdNotificationUseCase,
    val analytics: FirebaseAnalytics?
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsScreenUiState> =
        MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            launch {
                settingsRepository.readBoolean(SettingsKeys.NOTIFY_WORD_OF_THE_DAY)
                    .collect { state ->
                        _uiState.update {
                            it.copy(notifyWordOfTheDay = state)
                        }
                    }
            }

            launch {
                settingsRepository.readBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED)
                    .collect { state ->
                        _uiState.update {
                            Log.d("SettingsViewModel", "isHistoryDeActive: $state")
                            it.copy(isHistoryDeactivated = state)
                        }
                    }
            }

            analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "SettingsScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "SettingsScreen.kt")
            }
        }
    }

    fun toastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    suspend fun toggleHistory(state: Boolean) {
        Log.d("DeactivateHistory", "onCheckedChange: $state")

        settingsRepository.saveBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED, state)
        _uiState.update {
            it.copy(
                toastMessage = "History ${if (state) "disabled" else "enabled"}",
            )
        }
    }

    suspend fun clearHistory() {
        historyRepository.clearHistory()
        _uiState.update { it.copy(toastMessage = "History cleared") }
    }

    suspend fun clearFavourites() {
        favouriteRepository.clearFavorites()
        _uiState.update { it.copy(toastMessage = "Favorites cleared") }
    }

    suspend fun toggleNotifyWordOfTheDay(state: Boolean) {
        activateWotdNotificationUseCase.execute(state)
    }
}