package com.ayitinya.englishdictionary.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.favourite_words.FavouritesRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavouriteScreenUiState())
    val uiState: MutableStateFlow<FavouriteScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            favouritesRepository.getFavourites().collect { favourites ->
                _uiState.update {
                    it.copy(favourites = favourites)
                }
            }
        }
    }

    suspend fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(DefinitionScreenDestination(word = word), onlyIfResumed = true)
        }
    }
}