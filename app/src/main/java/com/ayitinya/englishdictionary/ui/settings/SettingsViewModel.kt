package com.ayitinya.englishdictionary.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.services.WotdService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val workManager = WorkManager.getInstance(context)
    private val _uiState: MutableStateFlow<SettingsScreenUiState> =
        MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.readBoolean("notify_word_of_the_day").collect {
                _uiState.value = SettingsScreenUiState(
                    notifyWordOfTheDay = it
                )
            }
        }
    }

    suspend fun toggleNotifyWordOfTheDay(state: Boolean) {

        settingsRepository.saveBoolean("notify_word_of_the_day", state)

        if (state) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()

            dueDate.set(Calendar.HOUR_OF_DAY, 8)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val constraints = Constraints
                .Builder()
                .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(requiresBatteryNotLow = true)
                .build()


            val workRequest = OneTimeWorkRequestBuilder<WotdService>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            settingsRepository.saveString("workRequestId", workRequest.id.toString())

            workManager.enqueueUniqueWork(
                "word_of_the_day",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        } else {
            val workRequestId = settingsRepository.readStringSync("workRequestId")
            val workRequestUuid = UUID.fromString(workRequestId)
            workManager.cancelWorkById(workRequestUuid)
        }
    }
}