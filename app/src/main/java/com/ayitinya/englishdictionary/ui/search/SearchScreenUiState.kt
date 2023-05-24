package com.ayitinya.englishdictionary.ui.search

import com.ayitinya.englishdictionary.data.dictionary.Dictionary
import com.ayitinya.englishdictionary.data.history.History

data class SearchScreenUiState(
    val searchQuery: String = "",
    val searchResults: List<Dictionary> = emptyList(),
    val history: List<History> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)
