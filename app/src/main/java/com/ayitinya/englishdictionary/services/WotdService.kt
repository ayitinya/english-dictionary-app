package com.ayitinya.englishdictionary.services

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.data.settings.SettingsRepository
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class WotdService @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val wotdRepository: DefaultWotdRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

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

        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        wotdRepository.updateWordOfTheDay()
        val wotd = wotdRepository.getWordOfTheDay() ?: return Result.failure()
        val definitionScreenRoute = DefinitionScreenDestination(word = wotd.word).route

        val intent = Intent(
            Intent.ACTION_VIEW,
            "app://com.ayitinya.englishdictionary/$definitionScreenRoute".toUri()
        )

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, context.getString(R.string.notification_channel_id)
        ).setSmallIcon(R.drawable.dictionary_1_svgrepo_com).setLargeIcon(
            BitmapFactory.decodeResource(
                applicationContext.resources, R.drawable.dictionary_1_svgrepo_com
            )
        ).setContentIntent(pendingIntent).setAutoCancel(true)
            .setContentTitle(context.getString(R.string.word_of_the_day))
            .setContentText(buildString {
                append(context.getString(R.string.notification_text))
                append(" ")
                append("'${wotd.word}'")
            }).setSmallIcon(R.drawable.dictionary_1_svgrepo_com)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, notificationBuilder.build())
        }

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 8)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints =
            Constraints.Builder().setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(requiresBatteryNotLow = true).build()


        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<WotdService>().setInitialDelay(
            timeDiff, TimeUnit.MILLISECONDS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        settingsRepository.saveString("workRequestId", workRequest.id.toString())

        return Result.success()
    }
}