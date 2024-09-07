package com.timerx.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.timerx.KEY_CREATE_TIMER
import com.timerx.KEY_RUN_TIMER_ID
import com.timerx.MainActivity
import com.timerx.R
import com.timerx.domain.timeFormatted
import com.timerx.settings.TimerXSettings
import com.timerx.ui.main.SortTimersBy
import com.timerx.ui.main.next
import com.timerx.widget.TimerXWidget.Companion.BIG_SQUARE
import com.timerx.widget.TimerXWidget.Companion.HORIZONTAL_RECTANGLE
import com.timerx.widget.TimerXWidget.Companion.VERTICAL_RECTANGLE
import com.timerx.widget.TimerXWidget.Companion.VERTICAL_RECTANGLE_LONG
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class TimerXWidget : GlanceAppWidget() {
    companion object {
        val HORIZONTAL_RECTANGLE = DpSize(110.dp, 50.dp)
        val VERTICAL_RECTANGLE = DpSize(50.dp, 110.dp)
        val VERTICAL_RECTANGLE_LONG = DpSize(50.dp, 350.dp)
        val BIG_SQUARE = DpSize(150.dp, 180.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(HORIZONTAL_RECTANGLE, VERTICAL_RECTANGLE, VERTICAL_RECTANGLE_LONG, BIG_SQUARE)
    )
    override val stateDefinition = TimerXWidgetStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MyContent()
            }
        }
    }
}

private fun DpSize.isThinMode(): Boolean {
    return this == VERTICAL_RECTANGLE
}

private fun SortTimersBy.drawable(): Int {
    return when (this) {
        SortTimersBy.SORT_ORDER -> R.drawable.sort_gradient
        SortTimersBy.RUN_DATE_ASC -> R.drawable.calendar_plus_gradient
        SortTimersBy.RUN_DATE_DESC -> R.drawable.calendar_minus_gradient
        SortTimersBy.NAME_ASC -> R.drawable.sort_alpha_down_gradient
        SortTimersBy.NAME_DESC -> R.drawable.sort_alpha_up_alt_gradient
        SortTimersBy.LENGTH_ASC -> R.drawable.sort_numeric_up_gradient
        SortTimersBy.LENGTH_DESC -> R.drawable.sort_numeric_down_alt_gradient
    }
}

@Composable
private fun MyContent() {
    val timerData = currentState<TimerWidgetInfo>()
    val size = LocalSize.current

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (size) {
            HORIZONTAL_RECTANGLE -> DebugText(text = "HR")
            VERTICAL_RECTANGLE -> DebugText(text = "VR")
            VERTICAL_RECTANGLE_LONG -> DebugText(text = "VRL")
            BIG_SQUARE -> DebugText(text = "BS")
        }
        Box(
            modifier = GlanceModifier.defaultWeight(),
            contentAlignment = Alignment.Center
        ) {
            when (timerData) {
                is TimerWidgetInfo.Available -> {
                    if (timerData.timers.isEmpty()) {
                        Text(
                            modifier = GlanceModifier.wrapContentSize(),
                            text = "No timers, add one below!",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                    } else {
                        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                            items(timerData.timers.size) { index ->
                                if (size.isThinMode()) {
                                    GlanceTimerThin(timerData.timers[index])
                                } else {
                                    GlanceTimer(timerData.timers[index])
                                }
                            }
                        }
                    }
                }

                TimerWidgetInfo.Loading -> {
                    CircularProgressIndicator()
                }

                is TimerWidgetInfo.Unavailable -> {
                    Text(text = "Unavailable")
                }
            }
        }
        if (size.isThinMode()) {
            WidgetButtonsThin(timerData)
        } else {
            WidgetButtons(timerData)
        }
    }
}

@Composable
private fun WidgetButtons(timerData: TimerWidgetInfo) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Spacer(modifier = GlanceModifier.defaultWeight())
        Image(
            modifier = GlanceModifier
                .clickable(actionStartActivity<MainActivity>())
                .size(48.dp),
            provider = ImageProvider(R.drawable.av_timer_gradient),
            contentDescription = "Home"
        )
        val coroutineScope = rememberCoroutineScope()
        if (timerData is TimerWidgetInfo.Available && timerData.timers.size > 1) {
            Spacer(modifier = GlanceModifier.defaultWeight())
            Image(
                modifier = GlanceModifier
                    .size(48.dp)
                    .clickable {
                        coroutineScope.launch {
                            val get = KoinPlatform.getKoin().get<TimerXSettings>()
                            val settings = get.settings.first()
                            get.setSortTimersBy(settings.sortTimersBy.next())
                        }
                    },
                contentDescription = "Sort",
                provider = ImageProvider(timerData.sortTimersBy.drawable())
            )
        }
        Spacer(modifier = GlanceModifier.defaultWeight())
        Image(
            modifier = GlanceModifier
                .defaultWeight()
                .size(48.dp)
                .clickable(
                    actionStartActivity<MainActivity>(
                        parameters =
                        actionParametersOf(
                            ActionParameters.Key<Boolean>(KEY_CREATE_TIMER) to true
                        )
                    )
                ),
            provider = ImageProvider(R.drawable.add_gradient),
            contentDescription = "Create"
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun WidgetButtonsThin(timerData: TimerWidgetInfo) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Image(
            modifier = GlanceModifier
                .clickable(actionStartActivity<MainActivity>())
                .size(48.dp),
            provider = ImageProvider(R.drawable.av_timer_gradient),
            contentDescription = "Home"
        )
        val coroutineScope = rememberCoroutineScope()
        if (timerData is TimerWidgetInfo.Available && timerData.timers.size > 1) {
            Spacer(modifier = GlanceModifier.height(8.dp))
            Image(
                modifier = GlanceModifier
                    .size(48.dp)
                    .clickable {
                        coroutineScope.launch {
                            val get = KoinPlatform.getKoin().get<TimerXSettings>()
                            val settings = get.settings.first()
                            get.setSortTimersBy(settings.sortTimersBy.next())
                        }
                    },
                contentDescription = "Sort",
                provider = ImageProvider(timerData.sortTimersBy.drawable())
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        Image(
            modifier = GlanceModifier
                .defaultWeight()
                .size(48.dp)
                .clickable(
                    actionStartActivity<MainActivity>(
                        parameters =
                        actionParametersOf(
                            ActionParameters.Key<Boolean>(KEY_CREATE_TIMER) to true
                        )
                    )
                ),
            provider = ImageProvider(R.drawable.add_gradient),
            contentDescription = "Create"
        )
    }
}

@Composable
private fun GlanceTimer(timerData: TimerData) {
    Column(
        modifier = GlanceModifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters =
                    actionParametersOf(
                        ActionParameters.Key<Long>(KEY_RUN_TIMER_ID) to timerData.id
                    )
                )
            ),
    ) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Column(modifier = GlanceModifier.wrapContentHeight().defaultWeight()) {
                Text(
                    text = timerData.name,
                    style = TextStyle(
                        fontSize = TextUnit(28f, TextUnitType.Sp),
                        color = GlanceTheme.colors.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = timerData.lastRun,
                    style = TextStyle(
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
            Text(
                text = timerData.length.timeFormatted(),
                style = TextStyle(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    color = GlanceTheme.colors.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
        Spacer(modifier = GlanceModifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
private fun GlanceTimerThin(timerData: TimerData) {
    Column(
        modifier = GlanceModifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters =
                    actionParametersOf(
                        ActionParameters.Key<Long>(KEY_RUN_TIMER_ID) to timerData.id
                    )
                )
            ),
    ) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = timerData.name,
            style = TextStyle(
                fontSize = TextUnit(28F, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurface
            ),
            maxLines = 1
        )
        Text(
            text = timerData.length.timeFormatted(),
            style = TextStyle(
                fontSize = TextUnit(16f, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurfaceVariant
            ),
            maxLines = 1
        )
        Text(
            text = timerData.lastRun,
            style = TextStyle(
                fontSize = TextUnit(16f, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurfaceVariant
            ),
            maxLines = 1
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
private fun HorizontalDivider(
    modifier: GlanceModifier = GlanceModifier,
    colorProvider: ColorProvider = GlanceTheme.colors.onSurfaceVariant,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier.fillMaxWidth()
            .height(thickness)
            .background(colorProvider = colorProvider)
    ) {}
}

@Composable
fun DebugText(text: String) {
    Text(text = text, style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant))
}
