package com.ayitinya.englishdictionary.ui.history

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ayitinya.englishdictionary.R
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object HistoryRoute

fun NavGraphBuilder.historyScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefinition: (String) -> Unit,
    onBack: () -> Unit
) {
    composable<HistoryRoute> {
        val viewModel = hiltViewModel<HistoryViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        HistoryScreen(
            modifier = modifier,
            uiState = uiState.value,
            deselectAllHistoryItems = viewModel::deselectAllHistoryItems,
            selectAllHistoryItems = viewModel::selectAllHistoryItems,
            deleteSelectedHistoryItems = {
                viewModel.viewModelScope.launch {
                    viewModel.deleteSelectedHistoryItems()
                }
            },
            selectHistoryItem = viewModel::selectHistoryItem,
            toggleWordSelection = viewModel::toggleWordSelection,
            toastShown = viewModel::toastShown,
            onNavigateToDefinition = onNavigateToDefinition,
            onBack = onBack
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    uiState: HistoryScreenUiState,
    deselectAllHistoryItems: () -> Unit,
    selectAllHistoryItems: () -> Unit,
    deleteSelectedHistoryItems: () -> Unit,
    selectHistoryItem: (String) -> Unit,
    toggleWordSelection: (String) -> Unit,
    toastShown: () -> Unit,
    onNavigateToDefinition: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = {
            Text(
                text = when (uiState.selectedHistory.isEmpty()) {
                    true -> stringResource(id = R.string.history)
                    false -> "${uiState.selectedHistory.size} ${stringResource(id = R.string.selected)}"
                }
            )
        }, navigationIcon = {
            AnimatedContent(
                targetState = uiState.selectedHistory.isNotEmpty(), transitionSpec = {
                    fadeIn(
                        animationSpec = tween(200)
                    ) togetherWith fadeOut(animationSpec = tween(200))
                }, label = ""
            ) {
                when (it) {
                    true -> {
                        IconButton(onClick = deselectAllHistoryItems) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    }

                    false -> IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            }

        }, actions = {
            if (uiState.selectedHistory.isNotEmpty()) {
                IconButton(onClick = selectAllHistoryItems) {
                    Checkbox(checked = uiState.selectedHistory.size == uiState.historyList.size,
                        onCheckedChange = {
                            if (it) {
                                selectAllHistoryItems()
                            } else {
                                deselectAllHistoryItems()
                            }
                        })
                }
                IconButton(onClick = deleteSelectedHistoryItems) {
                    Icon(
                        Icons.Filled.Delete, contentDescription = null
                    )
                }
            }
        }, scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            if (!uiState.isHistoryActive) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(id = R.string.history_deactivated),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (uiState.historyList.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(id = R.string.no_history),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(uiState.historyList) { history ->
                when (uiState.selectedHistory.isEmpty()) {
                    true -> {
                        ListItem(
                            headlineContent = { Text(text = history.word) },
                            modifier = Modifier.combinedClickable(onLongClick = {
                                selectHistoryItem(
                                    history.word
                                )
                            }) {
                                onNavigateToDefinition(history.word)
                            },
                        )
                    }

                    false -> {
                        ListItem(
                            leadingContent = {
                                Checkbox(checked = uiState.selectedHistory.contains(history),
                                    onCheckedChange = {
                                        toggleWordSelection(history.word)
                                    })
                            },
                            headlineContent = { Text(text = history.word) },
                            modifier = Modifier.combinedClickable(onLongClick = {
                                toggleWordSelection(history.word)
                            }) {
                                toggleWordSelection(history.word)
                            },
                        )
                    }
                }

            }
        }
    }

    BackHandler(enabled = uiState.selectedHistory.isNotEmpty()) {
        deselectAllHistoryItems()
    }

    LaunchedEffect(key1 = uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            Toast.makeText(context, uiState.toastMessage, Toast.LENGTH_SHORT).show()
            toastShown()
        }
    }
}