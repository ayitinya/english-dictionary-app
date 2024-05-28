package com.ayitinya.englishdictionary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ayitinya.englishdictionary.ui.about.AboutRoute
import com.ayitinya.englishdictionary.ui.about.OssLicencesRoute
import com.ayitinya.englishdictionary.ui.about.aboutScreen
import com.ayitinya.englishdictionary.ui.about.ossLicenses
import com.ayitinya.englishdictionary.ui.definition.DefinitionRoute
import com.ayitinya.englishdictionary.ui.definition.definitionScreen
import com.ayitinya.englishdictionary.ui.favourite.FavouritesRoute
import com.ayitinya.englishdictionary.ui.favourite.favouriteScreen
import com.ayitinya.englishdictionary.ui.history.HistoryRoute
import com.ayitinya.englishdictionary.ui.history.historyScreen
import com.ayitinya.englishdictionary.ui.home.Home
import com.ayitinya.englishdictionary.ui.home.homeScreen
import com.ayitinya.englishdictionary.ui.search.SearchRoute
import com.ayitinya.englishdictionary.ui.search.searchScreen
import com.ayitinya.englishdictionary.ui.settings.SettingsRoute
import com.ayitinya.englishdictionary.ui.settings.settingsScreen

@Composable
fun EnglishDictionaryNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(modifier = modifier, navController = navController, startDestination = Home) {
        aboutScreen(onBackButtonClick = navController::popBackStack,
            onNavigateToOSS = { navController.navigate(OssLicencesRoute) })

        ossLicenses(modifier = modifier, onBack = navController::popBackStack)

        favouriteScreen(onBack = navController::popBackStack,
            onNavigateToDefinition = { navController.navigate(DefinitionRoute(it)) })

        historyScreen(modifier = modifier,
            onBack = navController::popBackStack,
            onNavigateToDefinition = { navController.navigate(DefinitionRoute(it)) })

        definitionScreen(onBackButtonClick = navController::popBackStack,
            onNavigateToSearch = { navController.navigate(SearchRoute) })

        homeScreen(
            modifier = modifier,
            onNavigateToFavorites = { navController.navigate(FavouritesRoute) },
            onNavigateToDefinition = { word: String, isWotd: Boolean ->
                navController.navigate(DefinitionRoute(word = word, isWotd = isWotd))
            },
            onNavigateToSearch = { navController.navigate(SearchRoute) },
            onNavigateToSettings = { navController.navigate(SettingsRoute) },
            onNavigateToHistory = { navController.navigate(HistoryRoute) })

        searchScreen(
            modifier = modifier,
            onNavigateToDefinition = { navController.navigate(DefinitionRoute(it)) },
            onBack = navController::popBackStack
        )

        settingsScreen(
            modifier = modifier,
            onNavigateToAbout = { navController.navigate(AboutRoute) },
            onBack = navController::popBackStack,
        )

    }

}