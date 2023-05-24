package com.ayitinya.englishdictionary.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.data.word_of_the_day.source.Wotd
import com.ayitinya.englishdictionary.ui.destinations.FavouriteScreenDestination
import com.ayitinya.englishdictionary.ui.destinations.HistoryScreenDestination
import com.ayitinya.englishdictionary.ui.destinations.SearchScreenDestination
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
//                AnimatedVisibility(visible = uiState.wotd != null) {
//                    WordOfTheDay(wordOfTheDay = uiState.wotd!!)
//                }
                uiState.wotd?.let { WordOfTheDay(it, modifier = Modifier) }

                ListItem(headlineContent = { Text("Random Word") }, leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                    )
                }, modifier = Modifier.clickable {
                    viewModel.viewModelScope.launch {
                        viewModel.getRandomWord()
                        viewModel.navigateToDefinitionScreen(
                            uiState.randomWord!!.word, navController
                        )
                    }
                })
                ListItem(headlineContent = { Text("Favourite Words") }, leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                    )
                }, modifier = Modifier.clickable {
                    navController.navigate(
                        FavouriteScreenDestination, onlyIfResumed = true
                    )
                })
                ListItem(headlineContent = { Text("History") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable { navController.navigate(HistoryScreenDestination) })
            }
        }
    }
}


@Composable
private fun WordOfTheDay(wordOfTheDay: Wotd, modifier: Modifier = Modifier) {
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
                Text("WORD OF THE DAY")
                Text("${wordOfTheDay.date.dayOfMonth} ${wordOfTheDay.date.month} ${wordOfTheDay.date.year}")
            }
            Text(text = wordOfTheDay.word, style = MaterialTheme.typography.headlineLarge)
            Text(text = wordOfTheDay.glosses, style = MaterialTheme.typography.bodyLarge)
//            for (definition in wordOfTheDay.definitions) {
//                Text(
//                    text = definition.text, style = MaterialTheme.typography.bodyLarge
//                )
//            }

            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Learn More")
            }

        }
    }
}


//@Composable
//@Preview
//private fun WordOfTheDayPreview() {
//    WordOfTheDay(
//        WordApiResponse(
//            word = "Word", pdd = "24/04/2023", definitions = listOf(
//                Definition(text = "Definition 1", partOfSpeech = "Noun"),
//                Definition(text = "Definition 2", partOfSpeech = "Noun"),
//            )
//        )
//    )
//}


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
        Icon(Icons.Default.Search, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Enter the word to search for",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
    }
}