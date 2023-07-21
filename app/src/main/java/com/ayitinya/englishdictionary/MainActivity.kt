package com.ayitinya.englishdictionary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.coroutineScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ayitinya.englishdictionary.data.settings.source.local.readBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.readString
import com.ayitinya.englishdictionary.data.settings.source.local.saveBoolean
import com.ayitinya.englishdictionary.data.settings.source.local.saveString
import com.ayitinya.englishdictionary.services.UpdateWordOfTheDay
import com.ayitinya.englishdictionary.services.WotdNotificationService
import com.ayitinya.englishdictionary.ui.NavGraphs
import com.ayitinya.englishdictionary.ui.theme.EnglishDictionaryTheme
import com.ayitinya.englishdictionary.ui.widgets.WotdWidget
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.analytics.FirebaseAnalytics
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id), name, importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab")
        if (testLabSetting == "true") {
            FirebaseAnalytics.getInstance(this.baseContext).setAnalyticsCollectionEnabled(false)
            Toast.makeText(this.baseContext, "Disabling analytics collection", Toast.LENGTH_SHORT)
                .show()
        }

        createNotificationChannel()

        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            EnglishDictionaryTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                DisposableEffect(systemUiController, useDarkIcons) {
                    // Update all the system bar colors to be transparent, and use
                    // dark icons if we're in light theme
//                    systemUiController.setSystemBarsColor(
//                        color = Color.Transparent,
//                        darkIcons = useDarkIcons
//                    )
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent, darkIcons = useDarkIcons
                    )

                    // setStatusBarColor() and setNavigationBarColor() also exist

                    onDispose {}
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }

        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            Log.d("Main Activity", "Global scope")
            applicationContext.readString("app_version").take(1).collect {

                val currentVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageManager.getPackageInfo(
                        packageName, 0
                    ).longVersionCode
                } else {
                    @Suppress("DEPRECATION") packageManager.getPackageInfo(
                        packageName, 0
                    ).versionCode.toLong()
                }

                if (it == null) {
                    applicationContext.saveBoolean("first_open", true)
                } else {
                    applicationContext.saveBoolean("first_open", false)
                }

                if (it != currentVersion.toString()) {

                    applicationContext.saveString("app_version", currentVersion.toString())
                    WorkManager.getInstance(applicationContext).cancelAllWork()

                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        "update_word_of_the_day",
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequestBuilder<UpdateWordOfTheDay>().setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS
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
            timeDiff, TimeUnit.MILLISECONDS
        ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .setConstraints(constraints).build()

        applicationContext.saveString("workRequestId", workRequest.id.toString())

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "word_of_the_day", ExistingWorkPolicy.REPLACE, workRequest
        )
    }
}
