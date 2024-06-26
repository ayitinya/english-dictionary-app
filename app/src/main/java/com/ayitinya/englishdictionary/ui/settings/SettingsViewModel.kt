package com.ayitinya.englishdictionary.ui.settings

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
                settingsRepository.readBoolean(SettingsKeys.ETYMOLOGY_INITIAL_DISPLAY_COLLAPSED)
                    .collect { state ->
                        _uiState.update {
                            it.copy(etymologyCollapsed = state)
                        }
                    }
            }

            launch {
                settingsRepository.readBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED)
                    .collect { state ->
                        _uiState.update {
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

    fun toggleHistory(state: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED, state)
            _uiState.update {
                it.copy(
                    toastMessage = "History ${if (state) "disabled" else "enabled"}",
                )
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            _uiState.update { it.copy(toastMessage = "History cleared") }
        }
    }

    fun clearFavourites() {
        viewModelScope.launch {
            favouriteRepository.clearFavorites()
            _uiState.update { it.copy(toastMessage = "Favorites cleared") }
        }
    }

    fun toggleNotifyWordOfTheDay(state: Boolean) {
        viewModelScope.launch {
            activateWotdNotificationUseCase.execute(state)
        }
    }

    fun toggleEtymologyCollapsed(state: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveBoolean(SettingsKeys.ETYMOLOGY_INITIAL_DISPLAY_COLLAPSED, state)
            _uiState.update {
                it.copy(
                    toastMessage = "Etymology ${if (state) "collapsed" else "expanded"}",
                )
            }
        }
    }
}