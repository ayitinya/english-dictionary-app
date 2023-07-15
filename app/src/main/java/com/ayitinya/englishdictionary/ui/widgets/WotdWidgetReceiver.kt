package com.ayitinya.englishdictionary.ui.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.ayitinya.englishdictionary.data.word_of_the_day.source.DefaultWotdRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WotdWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var wotdRepository: DefaultWotdRepository

    override val glanceAppWidget: GlanceAppWidget
        get() = WotdWidget()

}