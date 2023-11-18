package com.ayitinya.englishdictionary.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.settings.source.local.SettingsKeys
import com.ayitinya.englishdictionary.data.settings.source.local.WorkManagerKeys
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import com.ayitinya.englishdictionary.ui.widgets.WotdWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class UpdateWordOfTheDay @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val wotdRepository: DefaultWotdRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > 25) {
            return Result.failure()
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        if (wotdRepository.getWordOfTheDay() == null) {
            return Result.retry()
        }

        val now = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(
            Calendar.MINUTE, 10
        ) // 12:10 AM, because the word of the day is updated at 12:00 AM
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(now)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - now.timeInMillis

        val constraints =
            Constraints.Builder().setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(requiresBatteryNotLow = true).build()


        val workRequest = OneTimeWorkRequestBuilder<UpdateWordOfTheDay>().setInitialDelay(
            timeDiff, TimeUnit.MILLISECONDS
        ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WorkManagerKeys.UPDATE_WORD_OF_THE_DAY.name, ExistingWorkPolicy.REPLACE, workRequest
        )


        settingsRepository.saveString(
            SettingsKeys.UPDATE_WORD_OF_THE_DAY_REQUEST_ID, workRequest.id.toString()
        )

        coroutineScope {
            launch {
                settingsRepository.readBoolean(SettingsKeys.NOTIFY_WORD_OF_THE_DAY).take(1)
                    .collect {
                        if (it) {
                            val currentTime =
                                now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
                            val targetTime = 8 * 60 // 8:00 AM converted to minutes

                            val initialDelay = if (currentTime < targetTime) {
                                val delayMinutes = targetTime - currentTime
                                delayMinutes.toLong()
                            } else {
                                0L
                            }

                            val notificationWorkRequest =
                                OneTimeWorkRequestBuilder<WotdNotificationService>().setInitialDelay(
                                    initialDelay, TimeUnit.MINUTES
                                ).build()

                            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                                WorkManagerKeys.NOTIFICATION_REQUEST_FOR_WORD_OF_THE_DAY.name,
                                ExistingWorkPolicy.REPLACE,
                                notificationWorkRequest
                            )
                        }
                    }
            }

            launch {
                WotdWidget().updateAll(applicationContext)
            }

        }

        return Result.success()
    }
}