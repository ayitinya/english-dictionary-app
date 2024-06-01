package com.ayitinya.englishdictionary.ui.definition

import com.ayitinya.englishdictionary.data.dictionary.Dictionary

data class DefinitionUiState(
    val isFavourite: Boolean = false,
    val word: String? = null,
    val showBottomModal: Boolean = false,
    val entries: Entries = Entries.Loading,
    val textToSpeechInitState: TextToSpeechInitState = TextToSpeechInitState.INITIALISING,
    val etymologyCollapsed: Boolean = false,
)

sealed class Entries {
    data object Loading : Entries()
    data class Success(val value: List<Dictionary>) : Entries()
    data class Error(val message: String) : Entries()
}
