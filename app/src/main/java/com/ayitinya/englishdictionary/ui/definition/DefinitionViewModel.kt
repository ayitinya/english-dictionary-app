package com.ayitinya.englishdictionary.ui.definition

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.data.settings.source.local.readBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.saveBoolean
import com.ayitinya.englishdictionary.domain.ActivateWotdNotificationUseCase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DefinitionViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val dictionaryRepository: DictionaryRepository,
    private val favouritesRepository: FavouritesRepository,
    private val historyRepository: HistoryRepository,
    private val activateWotdNotificationUseCase: ActivateWotdNotificationUseCase,
    private val settingsRepository: SettingsRepository,
    analytics: FirebaseAnalytics?,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var textToSpeech: TextToSpeech
    private val _navArgs = savedStateHandle.toRoute<DefinitionRoute>()

//    private var analytics: FirebaseAnalytics = Firebase.analytics

    private val _uiState = MutableStateFlow(DefinitionUiState(word = _navArgs.word))
    val uiState: StateFlow<DefinitionUiState> = _uiState

    init {
        textToSpeech = TextToSpeech(context) { initState ->
            onTextToSpeechInit(initState)
        }
        viewModelScope.launch {
            launch {
                context.readBoolean(SettingsKeys.ETYMOLOGY_INITIAL_DISPLAY_COLLAPSED)
                    .take(1)
                    .collect {
                        _uiState.update { uiState ->
                            uiState.copy(etymologyCollapsed = !it) // tbh i just had to add the ! to make it work
                        }

                    }
            }

            launch {
                _uiState.update {
                    it.copy(
                        entries = dictionaryRepository.getDictionaryEntries(_navArgs.word),
                        isFavourite = isFavourite(_navArgs.word)
                    )
                }
            }

            if (_navArgs.isWotd) {
                context.readBoolean(SettingsKeys.WOTD_MODAL_DISPLAY).take(1).collect { state ->
                    if (!state) {
                        context.saveBoolean(SettingsKeys.WOTD_MODAL_DISPLAY, true)
                        _uiState.update { it.copy(showBottomModal = true) }
                    }
                }
            }

            launch(Dispatchers.IO) {
                settingsRepository.readBoolean(SettingsKeys.IS_HISTORY_DEACTIVATED).take(1)
                    .collect {
                        if (!it) {
                            historyRepository.addHistory(_navArgs.word)
                        }
                    }
            }

            analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "DefinitionScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "DefinitionScreen.kt")
            }
        }
    }

    fun dismissModal() {
        _uiState.update { it.copy(showBottomModal = false) }
    }

    /* This is most likely an anti-pattern
    * textToSpeech can be considered UI and should be put in the UI layer
    * inability to shutdown textToSpeech resulted in leaving the code here and
    * destroying it when the view-model is destroyed */
    private fun onTextToSpeechInit(state: Int) {
        when (state) {
            TextToSpeech.SUCCESS -> {
                val setLocale = textToSpeech.setLanguage(Locale.ENGLISH)
                if (setLocale == TextToSpeech.LANG_MISSING_DATA || setLocale == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _uiState.update { it.copy(textToSpeechInitState = TextToSpeechInitState.FAILED) }
                    return
                }
                _uiState.update { it.copy(textToSpeechInitState = TextToSpeechInitState.READY) }
            }

            else -> {
                _uiState.update { it.copy(textToSpeechInitState = TextToSpeechInitState.FAILED) }
            }
        }
    }

    fun onSpeakClick() {
        val word = _uiState.value.word ?: return
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onIsFavouriteChange(state: Boolean) {
        _uiState.update { it.copy(isFavourite = state) }
        viewModelScope.launch {
            if (state) {
                insertFavourite(_navArgs.word)
            } else {
                removeFavourite(_navArgs.word)
            }
        }
    }

    private suspend fun isFavourite(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            favouritesRepository.isFavourite(word)
        }
    }

    private suspend fun insertFavourite(word: String) {
        withContext(Dispatchers.IO) {
            favouritesRepository.insertFavourite(word)
        }
    }

    private suspend fun removeFavourite(word: String) {
        withContext(Dispatchers.IO) {
            favouritesRepository.removeFavourite(word)
        }
    }

    fun activateWotdNotification() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                activateWotdNotificationUseCase.execute(true)
            }
        }
    }

    override fun onCleared() {
        textToSpeech.shutdown()
        super.onCleared()
    }
}