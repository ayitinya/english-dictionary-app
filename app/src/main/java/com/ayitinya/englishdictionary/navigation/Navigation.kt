package com.ayitinya.englishdictionary.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ayitinya.englishdictionary.ui.definition.DefinitionRoute
import com.ayitinya.englishdictionary.ui.definition.definitionScreen
import com.ayitinya.englishdictionary.ui.home.Home
import com.ayitinya.englishdictionary.ui.home.homeScreen

const val ANIMATION_TWEEN_DURATION = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EnglishDictionaryNavHost(modifier: Modifier = Modifier) {
    SharedTransitionLayout(modifier = modifier) {
        val navController = rememberNavController()

        NavHost(modifier = modifier,
            navController = navController,
            startDestination = Home,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(ANIMATION_TWEEN_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(ANIMATION_TWEEN_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(ANIMATION_TWEEN_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(ANIMATION_TWEEN_DURATION)
                )
            }
        ) {
            definitionScreen(
                onBackButtonClick = navController::popBackStack,
                sharedTransitionScope = this@SharedTransitionLayout
            )

            homeScreen(
                modifier = modifier,
                onNavigateToDefinition = { word: String, isWotd: Boolean ->
                    navController.navigate(DefinitionRoute(word = word, isWotd = isWotd))
                },
                sharedTransitionScope = this@SharedTransitionLayout
            )
        }
    }
}