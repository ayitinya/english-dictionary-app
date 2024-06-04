@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ayitinya.englishdictionary.ui.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefinition: (word: String, isWotd: Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<Home> {
        HomeScreen(
            modifier = modifier,
            onNavigateToDefinition = onNavigateToDefinition,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable
        )
    }
}


@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefinition: (String, Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Scaffold(modifier = modifier) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                WordOfTheDay(
                    wordOfTheDay = "hello",
                    onNavigateToDefinition = { onNavigateToDefinition(it, true) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
        }
    }
}


@Composable
private fun WordOfTheDay(
    wordOfTheDay: String,
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
            with(sharedTransitionScope) {
                Text(
                    text = wordOfTheDay,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.Companion.sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = wordOfTheDay),
                        animatedVisibilityScope = animatedContentScope
                    )
                )
            }
            TextButton(onClick = {
                onNavigateToDefinition(wordOfTheDay)
            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = "learn more")
            }
        }
    }
}