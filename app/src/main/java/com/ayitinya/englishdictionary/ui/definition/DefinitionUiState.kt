package com.ayitinya.englishdictionary.ui.definition

import com.ayitinya.englishdictionary.data.dictionary.DictionaryEntriesWithRelatedWords
import com.ayitinya.englishdictionary.data.test.TestEntry

data class DefinitionUiState(
    val isFavourite: Boolean = false,
    val word: String? = null,
    val showBottomModal: Boolean = false,
    val entries: DictionaryEntriesWithRelatedWords? = null,
    val test: TestEntry? = null,
    val textToSpeechInitState: TextToSpeechInitState = TextToSpeechInitState.INITIALISING
)
