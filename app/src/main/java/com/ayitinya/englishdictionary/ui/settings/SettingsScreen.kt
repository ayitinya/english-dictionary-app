package com.ayitinya.englishdictionary.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ayitinya.englishdictionary.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavGraphBuilder.settingsScreen(
    modifier: Modifier = Modifier, onNavigateToAbout: () -> Unit, onBack: () -> Unit
) {
    composable<SettingsRoute> {
        val viewModel: SettingsViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()

        SettingsScreen(
            modifier = modifier,
            uiState = uiState.value,
            onNavigateToAbout = {
                viewModel.analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, "AboutScreen")
                    param(FirebaseAnalytics.Param.SCREEN_CLASS, "AboutScreen.kt")
                }
                onNavigateToAbout()
            },
            toastShown = viewModel::toastShown,
            clearFavorites = viewModel::clearFavourites,
            toggleHistory = viewModel::toggleHistory,
            clearHistory = viewModel::clearHistory,
            onBuyMeACoffee = {
                viewModel.analytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_NAME, "Buy me a coffee")
                }
            },
            onShareApp = {
                viewModel.analytics?.logEvent(FirebaseAnalytics.Event.SHARE) {
                    param(FirebaseAnalytics.Param.ITEM_NAME, "Share app")
                }
            },
            toggleEtymologyCollapsed = viewModel::toggleEtymologyCollapsed,
            toggleNotifyWordOfTheDay = viewModel::toggleNotifyWordOfTheDay,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsScreenUiState,
    toggleNotifyWordOfTheDay: (Boolean) -> Unit,
    toggleEtymologyCollapsed: (Boolean) -> Unit,
    toggleHistory: (Boolean) -> Unit,
    clearHistory: () -> Unit,
    clearFavorites: () -> Unit,
    onBuyMeACoffee: () -> Unit,
    onShareApp: () -> Unit,
    toastShown: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onBack: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var permissionState: PermissionState? = null
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) toggleNotifyWordOfTheDay(true)
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
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.notify_word_of_the_day)) },
                    trailingContent = {
                        Switch(checked = uiState.notifyWordOfTheDay, onCheckedChange = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it) {
                                when (permissionState!!.status) {
                                    PermissionStatus.Granted -> {
                                        toggleNotifyWordOfTheDay(it)
                                    }

                                    else -> {
                                        if (permissionState.status.shouldShowRationale) {
                                            coroutineScope.launch {
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
                                toggleNotifyWordOfTheDay(it)
                            }
                        })
                    })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.collapse_etymology)) },
                    trailingContent = {
                        Switch(
                            checked = uiState.etymologyCollapsed,
                            onCheckedChange = toggleEtymologyCollapsed
                        )
                    })
                DeactivateHistory(state = uiState.isHistoryDeactivated, onConfirm = toggleHistory)
                ClearOption(
                    headlineContent = stringResource(id = R.string.clear_history),
                    supportingContent = stringResource(id = R.string.clear_history_rationale),
                    onConfirm = clearHistory
                )
                ClearOption(
                    headlineContent = stringResource(id = R.string.clear_favorites),
                    supportingContent = stringResource(id = R.string.clear_favorites_rationale),
                    onConfirm = clearFavorites
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ListItem(
                    modifier = Modifier.clickable {
                        val packageName = context.packageName
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
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
                    },
                    headlineContent = { Text(text = stringResource(id = R.string.rate_app)) },
                    supportingContent = { Text(text = stringResource(id = R.string.rate_app_message)) },
                )

                ListItem(
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, "English Dictionary")
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "https://play.google.com/store/apps/details?id=${context.packageName}"
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(Intent.createChooser(intent, "Share with"))
                        onShareApp()
                    },
                    headlineContent = { Text(text = stringResource(id = R.string.share_app)) },
                )

                ListItem(modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/ayitinya")
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Buy me a coffee")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onBuyMeACoffee()
                },
                    headlineContent = { Text(text = stringResource(id = R.string.buy_me_a_coffee)) })

                ListItem(modifier = Modifier.clickable {
                    onNavigateToAbout()

                }, headlineContent = { Text(text = stringResource(id = R.string.about)) })

            }
        }
    }

    LaunchedEffect(key1 = uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            Toast.makeText(context, uiState.toastMessage, Toast.LENGTH_SHORT).show()
            toastShown()
        }
    }
}

@Composable
private fun DeactivateHistory(
    state: Boolean, onConfirm: (state: Boolean) -> Unit
) {
    val dialogOpen = remember { mutableStateOf(false) }

    if (dialogOpen.value) {
        AlertDialog(onDismissRequest = { dialogOpen.value = false },
            title = { Text(text = stringResource(id = R.string.deactivate_history)) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.deactivate_history)
                )
            },
            text = { Text(text = stringResource(id = R.string.deactivate_history_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(true)
                    dialogOpen.value = false
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            })
    }

    ListItem(headlineContent = { Text(text = stringResource(id = R.string.deactivate_history)) },
        trailingContent = {
            Switch(checked = !state, onCheckedChange = {

                if (!it) {
                    dialogOpen.value = true
                } else {
                    onConfirm(false)
                }
            })
        })
}

@Composable
private fun ClearOption(
    headlineContent: String,
    supportingContent: String? = null,
    onConfirm: () -> Unit,
) {
    val dialogOpen = remember { mutableStateOf(false) }

    if (dialogOpen.value) {
        AlertDialog(onDismissRequest = { dialogOpen.value = false },
            title = { Text(text = headlineContent) },
            icon = {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = headlineContent)
            },
            text = {
                if (supportingContent != null) {
                    Text(text = supportingContent)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    dialogOpen.value = false
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            })
    }

    ListItem(
        modifier = Modifier.clickable { dialogOpen.value = true },
        headlineContent = { Text(text = headlineContent) },
    )
}