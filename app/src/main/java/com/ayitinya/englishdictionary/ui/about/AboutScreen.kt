package com.ayitinya.englishdictionary.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ayitinya.englishdictionary.BuildConfig
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.ui.destinations.OssLicencesDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AboutScreen(
    navController: DestinationsNavigator,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(id = R.string.about)) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        }, scrollBehavior = scrollBehavior
        )
    }) {
        LazyColumn(contentPadding = it) {
            item {
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.version)) },
                    supportingContent = { Text(text = "${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME}") })
                ListItem(headlineContent = { Text(text = "Privacy Policy") },
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("https://github.com/ayitinya/english-dictionary-app/blob/main/privacy.md")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    })
                ListItem(headlineContent = { Text(text = stringResource(id = R.string.oss_licenses)) },
                    modifier = Modifier.clickable {
                        navController.navigate(OssLicencesDestination)
                    })
            }
        }
    }
}