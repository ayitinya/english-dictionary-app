package com.ayitinya.englishdictionary.ui.favourite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.favourite_words.Favourite
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
    analytics: FirebaseAnalytics?
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavouriteScreenUiState())
    val uiState: StateFlow<FavouriteScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            favouritesRepository.getFavourites().collect { favourites ->
                _uiState.update {
                    it.copy(favourites = favourites)
                }
            }
            analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "FavouriteScreen")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "FavouriteScreen.kt")
            }
        }
    }

    fun toggleWordSelection(word: String) {
        _uiState.update { uiState ->
            uiState.copy(selectedFavourites = uiState.selectedFavourites.toMutableList().apply {
                if (contains(uiState.favourites.find { it.word == word })) {
                    remove(uiState.favourites.find { it.word == word })
                } else {
                    add(uiState.favourites.find { it.word == word }!!)
                }
            })
        }
    }

    suspend fun deleteSelectedFavoriteItems() {
        favouritesRepository.deleteSelectedFavoriteItems(_uiState.value.selectedFavourites)
        _uiState.update { uiState ->
            uiState.copy(
                selectedFavourites = emptyList<Favourite>().toMutableList(),
                toastMessage = "Delete Successful"
            )
        }
    }

    fun selectAllFavoriteItems() {
        _uiState.update { uiState ->
            uiState.copy(
                selectedFavourites = uiState.favourites.toMutableList()
            )
        }
    }

    fun deselectAllFavoriteItems() {
        _uiState.update { uiState ->
            uiState.copy(
                selectedFavourites = emptyList<Favourite>().toMutableList()
            )
        }
    }

    fun selectFavoriteItem(word: String) {
        _uiState.update { uiState ->
            uiState.copy(selectedFavourites = uiState.selectedFavourites.toMutableList().apply {
                add(
                    uiState.favourites.find { it.word == word }!!
                )
            })
        }
        Log.i("f", _uiState.value.selectedFavourites.toString())
    }

    fun toastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}