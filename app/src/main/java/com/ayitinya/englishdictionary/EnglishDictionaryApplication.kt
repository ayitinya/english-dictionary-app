package com.ayitinya.englishdictionary

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ayitinya.englishdictionary.data.settings.source.local.readBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.readString
import com.ayitinya.englishdictionary.data.settings.source.local.saveString
import com.ayitinya.englishdictionary.services.UpdateWordOfTheDay
import com.ayitinya.englishdictionary.services.WotdNotificationService
import com.ayitinya.englishdictionary.ui.widgets.WotdWidget
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class EnglishDictionaryApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.VERBOSE).build()
    }

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            applicationContext.readString("app_version").take(1).collect {

                val currentVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageManager.getPackageInfo(packageName, 0).longVersionCode
                } else {
                    @Suppress("DEPRECATION") packageManager.getPackageInfo(
                        packageName,
                        0
                    ).versionCode.toLong()
                }

                if (it != currentVersion.toString()) {

                    applicationContext.saveString("app_version", currentVersion.toString())
                    WorkManager.getInstance(applicationContext).cancelAllWork()

                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        "update_word_of_the_day",
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequestBuilder<UpdateWordOfTheDay>().setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL,
                            30,
                            TimeUnit.SECONDS
                        ).build()
                    )

                    applicationContext.readBoolean("notify_word_of_the_day").take(1)
                        .collect { state ->
                            if (state) {
                                startWotdNotificationService(applicationContext)
                            }
                        }

                }

            }
            WotdWidget().updateAll(applicationContext)

        }
    }

    private suspend fun startWotdNotificationService(applicationContext: Context) {
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


        val workRequest = OneTimeWorkRequestBuilder<WotdNotificationService>().setInitialDelay(
            timeDiff,
            TimeUnit.MILLISECONDS
        ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        applicationContext.saveString("workRequestId", workRequest.id.toString())

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "word_of_the_day", ExistingWorkPolicy.REPLACE, workRequest
        )
    }
}