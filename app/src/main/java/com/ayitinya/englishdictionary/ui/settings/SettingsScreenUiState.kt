package com.ayitinya.englishdictionary.ui.settings

data class SettingsScreenUiState(
    val notifyWordOfTheDay: Boolean = false,
    val isHistoryDeactivated: Boolean = false,
    val toastMessage: String? = null,
    val etymologyCollapsed: Boolean = false,
)
