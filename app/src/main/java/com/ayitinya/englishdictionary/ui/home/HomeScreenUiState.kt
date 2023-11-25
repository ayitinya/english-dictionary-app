package com.ayitinya.englishdictionary.ui.home

import com.ayitinya.englishdictionary.data.dictionary.Dictionary
import com.ayitinya.englishdictionary.data.word_of_the_day.source.Wotd
import com.ayitinya.englishdictionary.data.word_of_the_day.source.remote.WordApiResponse

data class HomeScreenUiState(
    val wordOfTheDay: WordApiResponse? = null,
    val wotd: Wotd? = null,
    val randomWord: Dictionary? = null,
    val searchResults: List<Dictionary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val dbInitialized: Boolean = false,
)
