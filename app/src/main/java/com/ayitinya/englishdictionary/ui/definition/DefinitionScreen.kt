package com.ayitinya.englishdictionary.ui.definition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.ui.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch

@Destination(navArgsDelegate = DefinitionScreenNavArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefinitionScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: DefinitionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .fillMaxSize(),
        topBar = {
            uiState.word?.let {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    word = it,
                    isFavourite = uiState.isFavourite,
                    onIsFavouriteChange = {
                        viewModel.viewModelScope.launch { viewModel.onIsFavouriteChange(it) }
                    },
                    navController = navController
                )
            }
        }

    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            uiState.entries?.let {
                items(it.dictionaryEntries) { dictionaryEntry ->
                    Definition(
                        pos = dictionaryEntry.pos,
                        glosses = dictionaryEntry.glosses,
                        example = dictionaryEntry.example,
                        sounds = dictionaryEntry.sounds
                    )
                }
            }
            item {
                Footer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    word: String,
    isFavourite: Boolean,
    onIsFavouriteChange: (Boolean) -> Unit,
    navController: DestinationsNavigator
) {
    LargeTopAppBar(title = { Text(text = word) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {

            PlainTooltipBox(tooltip = { Text("Add to favorites") }) {
                IconButton(
                    onClick = { onIsFavouriteChange(!isFavourite) },
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Localized Description"
                    )
                }
            }

            PlainTooltipBox(tooltip = { Text("Search") }) {
                IconButton(
                    onClick = { navController.navigate(SearchScreenDestination) },
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Localized Description"
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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = pos, style = MaterialTheme.typography.titleLarge)
                if (sounds != null) {
                    Text(text = "IPA: $sounds", style = MaterialTheme.typography.titleMedium)
                }
            }
            Text(text = glosses, style = MaterialTheme.typography.bodyLarge)
            if (example != null) {
                Column {
                    Text(text = "Example", style = MaterialTheme.typography.titleLarge)
                    Text(text = example, style = MaterialTheme.typography.bodyLarge)
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
    ) {
        Text(
            text = "Dictionary entries provided by Wiktionary, under the Creative Commons Attribution-ShareAlike License (CC-BY-SA)",
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
        isFavourite = false,
        onIsFavouriteChange = {},
        navController = EmptyDestinationsNavigator
    )
}