@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ayitinya.englishdictionary.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.data.word_of_the_day.source.Wotd
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit,
    onNavigateToDefinition: (word: String, isWotd: Boolean) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<Home> {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()

        HomeScreen(
            modifier = modifier,
            uiState = uiState.value,
            getRandomWord = {
                viewModel.viewModelScope.launch {
                    viewModel.getRandomWord()
                    uiState.value.randomWord?.let {
                        onNavigateToDefinition(it.word, false)
                    }
                }
            },
            getWordOfTheDay = viewModel::getWordOfTheDay,
            clearError = viewModel::clearError,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToDefinition = onNavigateToDefinition,
            onNavigateToFavorites = onNavigateToFavorites,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSettings = onNavigateToSettings,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeScreenUiState,
    clearError: () -> Unit,
    getRandomWord: () -> Unit,
    getWordOfTheDay: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToDefinition: (String, Boolean) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Scaffold(modifier = modifier, topBar = {
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBarButton(onNavigateToSearch = onNavigateToSearch)
        }
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                if (uiState.wotd == null) {
                    Card(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            if (uiState.isLoading) {
                                Text(
                                    text = stringResource(id = R.string.loading_wotd),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            16.dp
                                        )
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.failed_to_load_wotd),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            16.dp
                                        )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { getWordOfTheDay() }) {
                                        Text(text = stringResource(id = R.string.retry))
                                    }
                                }
                            }

                        }
                    }
                } else {
                    WordOfTheDay(
                        wordOfTheDay = uiState.wotd,
                        onNavigateToDefinition = { onNavigateToDefinition(it, true) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }

                ListItem(headlineContent = { Text(text = stringResource(id = R.string.random_word)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.random_word),
                        )
                    },
                    modifier = Modifier.clickable {
                        getRandomWord()
                    })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.favorites)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.favorites),
                        )
                    },
                    modifier = Modifier.clickable {
                        onNavigateToFavorites()
                    })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.history)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(id = R.string.history),
                        )
                    },
                    modifier = Modifier.clickable { onNavigateToHistory() })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.settings)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                        )
                    },
                    modifier = Modifier.clickable { onNavigateToSettings() })
            }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(
                context, uiState.error, Toast.LENGTH_LONG
            ).show()

            clearError()
        }
    }
}


@Composable
private fun WordOfTheDay(
    wordOfTheDay: Wotd,
    onNavigateToDefinition: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(id = R.string.word_of_the_day))
                Text("${wordOfTheDay.date.dayOfMonth} ${wordOfTheDay.date.month} ${wordOfTheDay.date.year}")
            }
            with(sharedTransitionScope) {
                Text(
                    text = wordOfTheDay.word,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.Companion.sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = wordOfTheDay.word),
                        animatedVisibilityScope = animatedContentScope
                    )
                )
            }
            wordOfTheDay.sense?.glosses?.firstOrNull()
                ?.let { Text(text = it, style = MaterialTheme.typography.bodyLarge) }

            TextButton(onClick = {
                onNavigateToDefinition(wordOfTheDay.word)
            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(id = R.string.learn_more))
            }
        }
    }
}

@Composable
@Preview
private fun SearchBarButton(onNavigateToSearch: () -> Unit = {}) {
    Button(
        onClick = onNavigateToSearch,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.search_hint))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.search_hint_details),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
    }
}