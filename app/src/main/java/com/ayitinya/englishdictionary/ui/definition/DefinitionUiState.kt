package com.ayitinya.englishdictionary.ui.definition

import com.ayitinya.englishdictionary.data.dictionary.DictionaryEntriesWithRelatedWords

data class DefinitionUiState(
    val isFavourite: Boolean = false,
    val word: String? = null,
    val entries: DictionaryEntriesWithRelatedWords? = null,
    val textToSpeechInitState: TextToSpeechInitState = TextToSpeechInitState.INITIALISING
)
