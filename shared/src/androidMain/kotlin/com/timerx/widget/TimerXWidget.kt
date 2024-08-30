package com.timerx.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.timerx.KEY_CREATE
import com.timerx.KEY_TIMER_ID
import com.timerx.MainActivity
import com.timerx.domain.timeFormatted

class TimerXWidget : GlanceAppWidget() {
    override val stateDefinition = TimerXWidgetStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MyContent()
            }
        }
    }
}

@Composable
private fun MyContent() {
    val prefs = currentState<TimerInfo>()

    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(GlanceTheme.colors.background),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (prefs) {
            is TimerInfo.Available -> {
                if (prefs.timers.isEmpty()) {
                    Text(text = "No timers, add one below!")
                } else {
                    prefs.timers.forEach {
                        GlanceTimer(it)
                    }
                }
            }

            TimerInfo.Loading -> {
                Text(text = "Loading")
            }

            is TimerInfo.Unavailable -> {
                Text(text = "Unavailable")
            }
        }
        Row(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                text = "Home",
                onClick = actionStartActivity<MainActivity>()
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            Button(
                text = "Create",
                onClick = actionStartActivity<MainActivity>(
                    parameters =
                    actionParametersOf(
                        ActionParameters.Key<Boolean>(KEY_CREATE) to true
                    )
                )
            )
        }
    }
}

@Composable
private fun GlanceTimer(timerData: TimerData) {
    Row(
        modifier = GlanceModifier.padding(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters =
                    actionParametersOf(
                        ActionParameters.Key<Long>(KEY_TIMER_ID) to timerData.id
                    )
                )
            ),
    ) {
        Text(
            text = timerData.name,
            style = TextStyle(color = GlanceTheme.colors.onSurface)
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        Text(
            text = timerData.length.timeFormatted(),
            style = TextStyle(color = GlanceTheme.colors.onSurface)
        )
    }
}