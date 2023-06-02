package com.ayitinya.englishdictionary.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.history.HistoryRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryScreenUiState())
    val uiState: MutableStateFlow<HistoryScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            historyRepository.observeHistory().collect {
                _uiState.value = HistoryScreenUiState(
                    historyList = it
                )
            }
        }
    }

    suspend fun navigateToDefinitionScreen(word: String, navController: DestinationsNavigator) {
        viewModelScope.launch {
            navController.navigate(DefinitionScreenDestination(word = word), onlyIfResumed = true)
        }
    }


}