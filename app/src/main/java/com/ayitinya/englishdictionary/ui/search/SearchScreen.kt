package com.ayitinya.englishdictionary.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun SearchScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Scaffold(modifier = modifier.fillMaxSize(),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                TextField(
                    keyboardActions = KeyboardActions(onDone = {
                        if (uiState.searchQuery.isNotBlank() && uiState.searchResults.isNotEmpty()) {
                            viewModel.viewModelScope.launch {
                                viewModel.navigateToDefinitionScreen(
                                    uiState.searchResults.first().word, navController
                                )
                            }
                        }
                    }),
                    value = uiState.searchQuery,
                    onValueChange = {
                        viewModel.viewModelScope.launch {
                            viewModel.updateSearchQuery(
                                it
                            )
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    placeholder = { Text(stringResource(id = R.string.search_hint_details)) },
                    singleLine = true,
                    leadingIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                )
            }
        }) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues, modifier = Modifier.background(Color.Transparent)
        ) {
            if (uiState.searchQuery.isNotEmpty()) {
                if (uiState.searchResults.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.no_results),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                items(uiState.searchResults) { result ->
                    ListItem(
                        headlineContent = { Text(result.word) },
                        modifier = Modifier.clickable {
                            viewModel.viewModelScope.launch {
                                viewModel.navigateToDefinitionScreen(
                                    result.word, navController
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.history),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(uiState.history) { history ->
                    ListItem(leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = stringResource(R.string.history)
                        )
                    }, headlineContent = { Text(history.word) }, modifier = Modifier.clickable {
                        viewModel.viewModelScope.launch {
                            viewModel.navigateToDefinitionScreen(
                                history.word, navController
                            )
                        }
                    }, colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                        headlineColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    )
                }
            }


        }

    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

}
