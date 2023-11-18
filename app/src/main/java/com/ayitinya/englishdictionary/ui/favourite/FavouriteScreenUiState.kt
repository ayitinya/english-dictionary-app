package com.ayitinya.englishdictionary.ui.favourite

import com.ayitinya.englishdictionary.data.favourite_words.Favourite

data class FavouriteScreenUiState(
    val favourites: List<Favourite> = emptyList(),
    val toastMessage: String? = null,
    val selectedFavourites: MutableList<Favourite> = emptyList<Favourite>().toMutableList(),
    val isLoading: Boolean = false,
    val error: String = "",
)
