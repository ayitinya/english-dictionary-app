package com.ayitinya.englishdictionary.ui.history

import com.ayitinya.englishdictionary.data.history.History

data class HistoryScreenUiState(
    val isLoading: Boolean = false,
    val historyList: List<History> = emptyList(),
    val error: String = "",
    val selectedHistory: MutableList<History> = emptyList<History>().toMutableList()
)
