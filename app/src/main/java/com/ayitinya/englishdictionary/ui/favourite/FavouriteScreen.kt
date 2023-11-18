package com.ayitinya.englishdictionary.ui.favourite

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.ayitinya.englishdictionary.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavouriteScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.favorites)) },
            navigationIcon = {
                AnimatedContent(
                    targetState = uiState.selectedFavourites.isNotEmpty(), transitionSpec = {
                        fadeIn(
                            animationSpec = tween(200)
                        ) togetherWith fadeOut(animationSpec = tween(200))
                    }, label = ""
                ) {
                    when (it) {
                        true -> {
                            IconButton(onClick = {
                                viewModel.viewModelScope.launch {
                                    viewModel.deselectAllFavoriteItems()
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                            }
                        }

                        else -> {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                            }
                        }
                    }
                }
            },
            actions = {
                if (uiState.selectedFavourites.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.selectAllFavoriteItems()
                        }
                    }) {
                        Checkbox(checked = uiState.selectedFavourites.size == uiState.favourites.size,
                            onCheckedChange = {
                                if (it) {
                                    viewModel.viewModelScope.launch {
                                        viewModel.selectAllFavoriteItems()
                                    }
                                } else {
                                    viewModel.viewModelScope.launch {
                                        viewModel.deselectAllFavoriteItems()
                                    }
                                }
                            })
                    }
                    IconButton(onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.deleteSelectedFavoriteItems()
                        }
                    }) {
                        Icon(
                            Icons.Filled.Delete, contentDescription = null
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            if (uiState.favourites.isEmpty()) {
                item {
                    Box(
                        modifier = modifier
                            .fillParentMaxHeight()
                            .fillMaxWidth(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_favorites),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            items(uiState.favourites) { history ->
                when (uiState.selectedFavourites.isEmpty()) {
                    true -> {
                        ListItem(
                            headlineContent = { Text(text = history.word) },
                            modifier = Modifier.combinedClickable(onLongClick = {
                                viewModel.selectFavoriteItem(history.word)
                            }) {
                                viewModel.navigateToDefinitionScreen(
                                    history.word, navController
                                )
                            },
                        )
                    }

                    false -> {
                        ListItem(
                            leadingContent = {
                                Checkbox(checked = uiState.selectedFavourites.contains(history),
                                    onCheckedChange = {
                                        viewModel.toggleWordSelection(history.word)
                                    })
                            },
                            headlineContent = { Text(text = history.word) },
                            modifier = Modifier.combinedClickable(onLongClick = {
                                viewModel.toggleWordSelection(history.word)
                            }) {
                                viewModel.toggleWordSelection(history.word)
                            },
                        )
                    }
                }

            }
        }
    }

    BackHandler(enabled = uiState.selectedFavourites.isNotEmpty()) {
        viewModel.viewModelScope.launch {
            viewModel.deselectAllFavoriteItems()
        }
    }

    LaunchedEffect(key1 = uiState.toastMessage) {
        println("toastMessage: ${uiState.toastMessage}")
        if (uiState.toastMessage != null) {
            Toast.makeText(context, uiState.toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.toastShown()
        }
    }
}
