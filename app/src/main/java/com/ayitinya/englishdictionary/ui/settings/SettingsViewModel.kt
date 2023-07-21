package com.ayitinya.englishdictionary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.domain.ActivateWotdNotificationUseCase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val activateWotdNotificationUseCase: ActivateWotdNotificationUseCase,
    val analytics: FirebaseAnalytics
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsScreenUiState> =
        MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.readBoolean("notify_word_of_the_day").collect {
                _uiState.value = SettingsScreenUiState(
                    notifyWordOfTheDay = it
                )
            }
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "SettingsScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "SettingsScreen.kt")
            }
        }
    }

    suspend fun toggleNotifyWordOfTheDay(state: Boolean) {
        activateWotdNotificationUseCase.execute(state)
    }
}