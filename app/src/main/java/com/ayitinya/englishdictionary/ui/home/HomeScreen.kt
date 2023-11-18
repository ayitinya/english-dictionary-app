package com.ayitinya.englishdictionary.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.data.word_of_the_day.source.Wotd
import com.ayitinya.englishdictionary.ui.destinations.FavouriteScreenDestination
import com.ayitinya.englishdictionary.ui.destinations.HistoryScreenDestination
import com.ayitinya.englishdictionary.ui.destinations.SearchScreenDestination
import com.ayitinya.englishdictionary.ui.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = modifier, topBar = {
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBarButton(navController = navController)
        }
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                AnimatedVisibility(visible = uiState.wotd != null) {
                    WordOfTheDay(
                        wordOfTheDay = uiState.wotd!!,
                        viewModel = viewModel,
                        navController = navController
                    )
                }

                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.random_word)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.random_word),
                        )
                    },
                    modifier = Modifier.clickable {
                        viewModel.viewModelScope.launch {
                            viewModel.getRandomWord()
                            uiState.randomWord?.let {
                                viewModel.navigateToDefinitionScreen(
                                    it.word, navController = navController
                                )
                            }
                        }
                    })
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.favorites)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.favorites),
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(
                            FavouriteScreenDestination, onlyIfResumed = true
                        )
                    })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.history)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(id = R.string.history),
                        )
                    },
                    modifier = Modifier.clickable { navController.navigate(HistoryScreenDestination) })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.settings)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                        )
                    },
                    modifier = Modifier.clickable { navController.navigate(SettingsScreenDestination) })
            }
        }
    }
}


@Composable
private fun WordOfTheDay(
    wordOfTheDay: Wotd,
    viewModel: HomeScreenViewModel,
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier
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
            Text(text = wordOfTheDay.word, style = MaterialTheme.typography.headlineLarge)
            wordOfTheDay.sense?.glosses?.firstOrNull()
                ?.let { Text(text = it, style = MaterialTheme.typography.bodyLarge) }

            TextButton(onClick = {
                viewModel.navigateToDefinitionScreen(
                    word = wordOfTheDay.word,
                    fromWotd = true,
                    navController
                )
            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(id = R.string.learn_more))
            }

        }
    }
}

@Composable
@Preview
private fun SearchBarButton(navController: DestinationsNavigator = EmptyDestinationsNavigator) {
    Button(
        onClick = { navController.navigate(SearchScreenDestination, onlyIfResumed = true) },
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