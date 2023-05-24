package com.ayitinya.englishdictionary.ui.favourite

import com.ayitinya.englishdictionary.data.favourite_words.Favourite

data class FavouriteScreenUiState(
    val favourites: List<Favourite> = emptyList(),
)
