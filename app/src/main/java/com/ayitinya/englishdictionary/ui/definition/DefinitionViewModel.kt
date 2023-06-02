package com.ayitinya.englishdictionary.ui.definition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.dictionary.DictionaryRepository
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DefinitionViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val favouritesRepository: FavouritesRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _navArgs: DefinitionScreenNavArgs =
        DefinitionScreenDestination.argsFrom(savedStateHandle)

    private val _uiState = MutableStateFlow(DefinitionUiState(word = _navArgs.word))
    val uiState: StateFlow<DefinitionUiState> = _uiState


    init {
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
}