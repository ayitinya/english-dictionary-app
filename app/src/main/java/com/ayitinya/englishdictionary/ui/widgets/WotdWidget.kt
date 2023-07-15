package com.ayitinya.englishdictionary.ui.widgets

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.ayitinya.englishdictionary.R
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import com.ayitinya.englishdictionary.ui.destinations.DefinitionScreenDestination
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class WotdWidget() : GlanceAppWidget() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WotdWidgetEntryPoint {
        fun wotdRepository(): DefaultWotdRepository
    }

    private lateinit var analytics: FirebaseAnalytics

    companion object {
        private val SMALL_BOX = DpSize(90.dp, 90.dp)
        private val BIG_BOX = DpSize(180.dp, 180.dp)
        private val VERY_BIG_BOX = DpSize(300.dp, 300.dp)
        private val ROW = DpSize(180.dp, 48.dp)
        private val LARGE_ROW = DpSize(300.dp, 48.dp)
        private val COLUMN = DpSize(48.dp, 180.dp)
        private val LARGE_COLUMN = DpSize(48.dp, 300.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SMALL_BOX, BIG_BOX, VERY_BIG_BOX, ROW, LARGE_ROW, COLUMN, LARGE_COLUMN)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(context, WotdWidgetEntryPoint::class.java)
        val wotdRepository = hiltEntryPoint.wotdRepository()
        val wotd = wotdRepository.getWordOfTheDay()
        analytics = Firebase.analytics

        provideContent {
            Log.d("WotdWidget", "provideGlance: ${LocalSize.current.height}")

            GlanceTheme {
                if (wotd != null) {
                    val definitionScreenRoute = DefinitionScreenDestination(word = wotd.word).route
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "app://com.ayitinya.englishdictionary/$definitionScreenRoute".toUri()
                    )
                    intent.apply {
                        `package` = context.packageName
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    Column(
                        modifier = GlanceModifier.fillMaxSize().padding(16.dp)
                            .background(GlanceTheme.colors.background).clickable {
                                analytics.logEvent("wotd_widget_clicked") {
                                    param("word", wotd.word)
                                }
                                context.startActivity(intent)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (LocalSize.current) {
                            SMALL_BOX, ROW, LARGE_ROW -> {
                                Text(
                                    text = context.getString(R.string.word_of_the_day),
                                    style = TextStyle(
                                        color = GlanceTheme.colors.secondary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp
                                    ),
                                    modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp)
                                )
                                Text(
                                    text = wotd.word,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(value = 18f, type = TextUnitType.Sp)
                                    ),
                                    modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
                                    maxLines = 2,
                                )
                            }

                            BIG_BOX, VERY_BIG_BOX, COLUMN, LARGE_COLUMN -> {
                                Text(
                                    text = context.getString(R.string.word_of_the_day),
                                    style = TextStyle(
                                        color = GlanceTheme.colors.secondary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    ),
                                    modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp)
                                )
                                Text(
                                    text = wotd.word,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 18.sp
                                    ),
                                    modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
                                    maxLines = 2,
                                )
                                Text(
                                    text = wotd.glosses, maxLines = 2, style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        textAlign = TextAlign.Center,
                                    ), modifier = GlanceModifier.fillMaxWidth()
                                )
                            }

                            else -> {
                                throw IllegalArgumentException("Invalid size not matching the provided ones")
                            }
                        }
                    }

                } else {
                    Box(
                        modifier = GlanceModifier.fillMaxSize().padding(16.dp)
                            .background(GlanceTheme.colors.background),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Error loading word of the day", style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                textAlign = TextAlign.Center,
                            ), modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
}