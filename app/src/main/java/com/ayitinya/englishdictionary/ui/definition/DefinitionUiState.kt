package com.ayitinya.englishdictionary.ui.definition

import com.ayitinya.englishdictionary.data.dictionary.Dictionary

data class DefinitionUiState(
    val isFavourite: Boolean = false,
    val word: String? = null,
    val showBottomModal: Boolean = false,
    val entries: List<Dictionary>? = null,
    val textToSpeechInitState: TextToSpeechInitState = TextToSpeechInitState.INITIALISING
)
