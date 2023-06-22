package com.ayitinya.englishdictionary.ui.definition

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var textToSpeech: TextToSpeech
    private val _navArgs: DefinitionScreenNavArgs =
        DefinitionScreenDestination.argsFrom(savedStateHandle)

    private val _uiState = MutableStateFlow(DefinitionUiState(word = _navArgs.word))
    val uiState: StateFlow<DefinitionUiState> = _uiState


    init {
        textToSpeech = TextToSpeech(context) { initState ->
            onTextToSpeechInit(initState)
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    entries = dictionaryRepository.getDictionaryEntries(_navArgs.word),
                    isFavourite = isFavourite(_navArgs.word)
                )
            }

            historyRepository.addHistory(_navArgs.word)

        }
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

    suspend fun onIsFavouriteChange(state: Boolean) {
        _uiState.update { it.copy(isFavourite = state) }
        if (state) {
            insertFavourite(_navArgs.word)
        } else {
            removeFavourite(_navArgs.word)
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

    override fun onCleared() {
        textToSpeech.shutdown()
        super.onCleared()
    }
}