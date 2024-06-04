@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ayitinya.englishdictionary.ui.definition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.serialization.Serializable

@Serializable
data class DefinitionRoute(val word: String, val isWotd: Boolean = false)

fun NavGraphBuilder.definitionScreen(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<DefinitionRoute>(deepLinks = listOf(navDeepLink {
        uriPattern = "app://com.ayitinya.englishdictionary/{word}"
    })) {

        DefinitionScreen(
            modifier = modifier,
            onBackButtonClick = onBackButtonClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
)
@Composable
private fun DefinitionScreen(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                word = "hello",
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        },
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) { }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun TopAppBar(
    word: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    LargeTopAppBar(title = {
        BoxWithConstraints {
            if (maxWidth > 300.dp) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    with(sharedTransitionScope) {
                        Text(
                            text = word, modifier = Modifier
                                .sharedElement(
                                    sharedTransitionScope.rememberSharedContentState(key = "hello"),
                                    animatedContentScope
                                )
                                .weight(4f)
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
                }
            }
        }

    })
}
