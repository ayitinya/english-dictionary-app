package com.ayitinya.englishdictionary.ui.definition

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.ui.destinations.SearchScreenDestination
import com.ayitinya.englishdictionary.ui.widgets.WotdWidgetReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch

@Destination(
    navArgsDelegate = DefinitionScreenNavArgs::class,
    deepLinks = [DeepLink(uriPattern = "app://com.ayitinya.englishdictionary/$FULL_ROUTE_PLACEHOLDER")]
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DefinitionScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: DefinitionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current.applicationContext

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }


    if (uiState.showBottomModal) {
        var permissionState: PermissionState? = null

        val requestPermissionLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                if (it) {
                    viewModel.viewModelScope.launch { viewModel.activateWotdNotification() }
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionState =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        }
        RequestNotificationAccessModal(
            onDismissRequest = viewModel::dismissModal,
            onActivateWotdNotificationButtonClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when (permissionState!!.status) {
                        PermissionStatus.Granted -> {
                            viewModel.viewModelScope.launch { viewModel.activateWotdNotification() }
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
                    viewModel.viewModelScope.launch { viewModel.activateWotdNotification() }
                }
            },
        )
    }

    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            uiState.word?.let {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    word = it,
                    navController = navController,
                    isTextToSpeechReady = uiState.textToSpeechInitState == TextToSpeechInitState.READY
                ) { viewModel.onSpeakClick() }
            }
        },
        floatingActionButton = {
            uiState.word?.let {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = if (uiState.isFavourite) stringResource(id = R.string.remove_from_favorites) else stringResource(
                                id = R.string.add_to_favorites
                            )
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (uiState.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (uiState.isFavourite) stringResource(id = R.string.remove_from_favorites) else stringResource(
                                id = R.string.add_to_favorites
                            )
                        )
                    },
                    expanded = lazyListState.isScrollingUp(),
                    onClick = { viewModel.viewModelScope.launch { viewModel.onIsFavouriteChange(!uiState.isFavourite) } },
                )
            }
        },

        ) { paddingValues ->
        AnimatedVisibility(visible = uiState.entries != null) {
            LazyColumn(contentPadding = paddingValues, state = lazyListState) {
                uiState.entries?.let {
                    if (it.isNotEmpty() && it.first().etymology != null) {
                        item {
                            ExpandableCard(
                                initialState = viewModel.uiState.value.etymologyCollapsed,
                                title = stringResource(R.string.etymology)
                            ) {
                                Text(
                                    text = it.first().etymology!!,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
//                            Card(
//                                modifier = modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                            ) {
//                                SelectionContainer {
//                                    Column(
//                                        verticalArrangement = Arrangement.spacedBy(12.dp),
//                                        modifier = Modifier.padding(16.dp)
//                                    ) {
//                                        Text(
//                                            text = stringResource(R.string.etymology),
//                                            style = MaterialTheme.typography.titleLarge
//                                        )
//                                        Text(
//                                            text = it.first().etymology!!,
//                                            style = MaterialTheme.typography.bodyLarge
//                                        )
//                                    }
//                                }
//                            }
                        }

                    }
                    it.forEach { entry ->
                        entry.senses.forEach { sense ->
                            item {
                                Definition(pos = entry.pos,
                                    glosses = sense.glosses.fold("") { acc, s -> acc.plus("$s\n") }
                                        .trim(),
                                    example = sense.examples.fold("") { acc, s -> acc.plus("$s\n") }
                                        .trim(),
                                    sounds = entry.sound)
                            }
                        }
                    }
                }
                item {
                    Footer()
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    word: String,
    navController: DestinationsNavigator,
    isTextToSpeechReady: Boolean = false,
    onSpeakClick: () -> Unit,
) {

    LargeTopAppBar(title = {
        BoxWithConstraints {
            if (maxWidth > 300.dp) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = word, modifier = Modifier.weight(4f))
                    IconButton(
                        onClick = onSpeakClick,
                        enabled = isTextToSpeechReady,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = stringResource(id = R.string.play_button)
                        )
                    }
                }

            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = word,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(4f)
                    )
                    IconButton(
                        onClick = onSpeakClick,
                        enabled = isTextToSpeechReady,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = stringResource(id = R.string.play_button)
                        )
                    }
                }
            }
        }


    }, scrollBehavior = scrollBehavior, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
        }
    }, actions = {


        PlainTooltipBox(tooltip = { Text("Search") }) {
            IconButton(
                onClick = { navController.navigate(SearchScreenDestination) },
                modifier = Modifier.tooltipAnchor()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        }
    })

}

@Composable
private fun Definition(
    pos: String, glosses: String, example: String?, sounds: String?, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SelectionContainer {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = pos, style = MaterialTheme.typography.titleLarge)
                    if (sounds != null) {
                        Text(
                            text = "${stringResource(id = R.string.ipa)}: $sounds",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Text(text = glosses, style = MaterialTheme.typography.bodyLarge)
                if (!example.isNullOrEmpty()) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.example),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(text = example, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 64.dp)
    ) {
        Text(
            text = stringResource(id = R.string.attribution),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
@Preview
private fun DefinitionPreview() {
    Definition(
        pos = "noun",
        glosses = "A word with a very long meaning",
        example = "here is an example of the word",
        sounds = "/fck/"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun TopAppBarPreview() {
    TopAppBar(
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        word = "Word",
        navController = EmptyDestinationsNavigator,
        isTextToSpeechReady = true
    ) {}
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun RequestNotificationAccessModal(
    onDismissRequest: () -> Unit = {},
    onActivateWotdNotificationButtonClick: () -> Unit = {},
    onAddWidgetButtonClick: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = {},
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        var currentState by rememberSaveable {
            mutableStateOf("notification_request")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentState == "notification_request") {


                Text(
                    text = "Get notifications for word of the day",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(64.dp)
                )
                Text(
                    text = "Get notified each day when the word of the day is updated. It can be changed later in settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    TextButton(onClick = {
                        currentState = "widget_request"
                    }, Modifier.fillMaxWidth()) {
                        Text(text = "Not now")
                    }
                    Button(
                        onClick = {
                            currentState = "widget_request"
                            onActivateWotdNotificationButtonClick()
                        },
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "Enable")
                    }
                }
            } else {
                Text(
                    text = "Word of the day widget",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Outlined.Widgets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(64.dp)
                )
                Text(
                    text = "Add the widget to home screen to quickly view the word of the day",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    val appWidgetManager =
                        AppWidgetManager.getInstance(LocalContext.current.applicationContext)
                    val provider = ComponentName(
                        LocalContext.current.applicationContext, WotdWidgetReceiver::class.java
                    )
                    val intent = Intent(
                        LocalContext.current.applicationContext, WotdWidgetReceiver::class.java
                    ).apply {

                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        LocalContext.current.applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )

                    TextButton(onClick = onDismissRequest, Modifier.fillMaxWidth()) {
                        Text(text = "Not now")
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Button(
                            onClick = {
                                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                    onAddWidgetButtonClick()
                                    appWidgetManager.requestPinAppWidget(
                                        provider, null, pendingIntent
                                    )
                                }
                                onDismissRequest()
                            },
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = "Add")
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismissRequest()
                            },
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = "Ok")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    initialState: Boolean = true,
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initialState) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable { expanded = !expanded })
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                content()
            }
        }
    }
}