package com.ayitinya.englishdictionary.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun SettingsScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val context = LocalContext.current.applicationContext

    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var permissionState: PermissionState? = null
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.viewModelScope.launch { viewModel.toggleNotifyWordOfTheDay(true) }
            }
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionState =
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(modifier = modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                val currentActivity = LocalContext.current as Activity
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.notify_word_of_the_day)) },
                    trailingContent = {
                        Switch(checked = uiState.notifyWordOfTheDay, onCheckedChange = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it) {
                                when (permissionState!!.status) {
                                    PermissionStatus.Granted -> {
                                        viewModel.viewModelScope.launch {
                                            viewModel.toggleNotifyWordOfTheDay(it)
                                        }
                                    }

                                    else -> {
                                        if (permissionState.status.shouldShowRationale) {
                                            viewModel.viewModelScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "This feature requires notification permission",
                                                    actionLabel = "Go to settings",
                                                )

                                                if (result == SnackbarResult.ActionPerformed) {
                                                    val intent = Intent(
                                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                        Uri.fromParts(
                                                            "package", context.packageName, null
                                                        )
                                                    )
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    context.startActivity(intent)

                                                }
                                            }
                                        } else {
                                            requestPermissionLauncher.launch(
                                                Manifest.permission.POST_NOTIFICATIONS
                                            )
                                        }
                                    }
                                }
                            } else {
                                viewModel.viewModelScope.launch {
                                    viewModel.toggleNotifyWordOfTheDay(it)
                                }
                            }
                        })
                    })
                ListItem(
                    modifier = Modifier.clickable {
                        val manager = ReviewManagerFactory.create(context)
                        val request = manager.requestReviewFlow()
                        request.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.viewModelScope.launch {
                                    manager.launchReview(currentActivity, task.result)
                                }
                            } else {
                                // There was some problem, log or handle the error code.
//                                @ReviewErrorCode val reviewErrorCode =
//                                    (task.exception as ReviewException).errorCode
                                Firebase.crashlytics.recordException(task.exception!!)
                                val packageName = context.packageName
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.setPackage("com.android.vending") // Specify the package name of the Play Store app
                                    context.startActivity(intent)
                                } catch (e: android.content.ActivityNotFoundException) {
                                    // If the Play Store app is not installed, open the Play Store website
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            }
                        }
                    },
                    headlineContent = { Text(text = stringResource(id = R.string.rate_app)) },
                )
            }
        }
    }

}