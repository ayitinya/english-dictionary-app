package com.ayitinya.englishdictionary.domain

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.data.settings.source.local.WorkManagerKeys
import com.ayitinya.englishdictionary.services.WotdNotificationService
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ViewModelScoped
class ActivateWotdNotificationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext context: Context
) {
    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val workManager = WorkManager.getInstance(context)

    suspend fun execute(state: Boolean) {
        settingsRepository.saveBoolean(SettingsKeys.NOTIFY_WORD_OF_THE_DAY, state)
        analytics.logEvent("toggle_word_of_the_day_notification") {
            param("state", state.toString())
        }

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


            val workRequest = OneTimeWorkRequestBuilder<WotdNotificationService>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            settingsRepository.saveString(SettingsKeys.WORK_REQUEST_ID, workRequest.id.toString())

            workManager.enqueueUniqueWork(
                WorkManagerKeys.NOTIFICATION_REQUEST_FOR_WORD_OF_THE_DAY.name,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        } else {
            val workRequestId = settingsRepository.readStringSync(SettingsKeys.WORK_REQUEST_ID)
            try {
                val workRequestUuid = UUID.fromString(workRequestId)
                workManager.cancelWorkById(workRequestUuid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}