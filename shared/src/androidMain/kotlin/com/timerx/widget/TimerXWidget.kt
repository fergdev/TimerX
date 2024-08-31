package com.timerx.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
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
    val timerData = currentState<TimerInfo>()

    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(GlanceTheme.colors.background),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = GlanceModifier.defaultWeight()) {
            when (timerData) {
                is TimerInfo.Available -> {
                    if (timerData.timers.isEmpty()) {
                        Text(text = "No timers, add one below!")
                    } else {
                        LazyColumn {
                            items(timerData.timers.size) { index ->
                                GlanceTimer(timerData.timers[index])
                            }
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
        }
        Row(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
        modifier = GlanceModifier.padding(16.dp).fillMaxWidth()
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
            modifier = GlanceModifier.defaultWeight(),
            text = timerData.name,
            style = TextStyle(
                fontSize = TextUnit(28f, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurface
            )
        )
        Text(
            text = timerData.length.timeFormatted(),
            style = TextStyle(
                fontSize = TextUnit(16f, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurfaceVariant
            )
        )
    }
}