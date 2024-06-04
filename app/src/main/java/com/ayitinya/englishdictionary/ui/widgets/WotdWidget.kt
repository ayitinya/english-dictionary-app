package com.ayitinya.englishdictionary.ui.widgets

import android.content.Context
import android.content.Intent
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
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.ayitinya.englishdictionary.R

class WotdWidget : GlanceAppWidget() {

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
        val wotd = "hello"

        provideContent {
            GlanceTheme {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "app://com.ayitinya.englishdictionary/${wotd}".toUri()
                )
                intent.apply {
                    `package` = context.packageName
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                Column(
                    modifier = GlanceModifier.fillMaxSize().padding(16.dp)
                        .background(GlanceTheme.colors.background).clickable {
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
                                text = wotd,
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
                        }

                        else -> {
                            throw IllegalArgumentException("Invalid size not matching the provided ones")
                        }
                    }
                }

            }

        }
    }
}