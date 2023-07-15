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
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import com.ayitinya.englishdictionary.ui.widgets.WotdWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class UpdateWordOfTheDay @AssistedInject constructor(
    @Assisted private val context: Context,
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

        WotdWidget().updateAll(context)

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 10)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(requiresBatteryNotLow = true).build()


        val workRequest = OneTimeWorkRequestBuilder<UpdateWordOfTheDay>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("update_word_of_the_day", ExistingWorkPolicy.REPLACE, workRequest)


        settingsRepository.saveString("updateWordOfTheDayRequestId", workRequest.id.toString())

        return Result.success()
    }
}