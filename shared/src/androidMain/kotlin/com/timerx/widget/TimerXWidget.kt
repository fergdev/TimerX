package com.timerx.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.timerx.KEY_CREATE_TIMER
import com.timerx.KEY_RUN_TIMER_ID
import com.timerx.MainActivity
import com.timerx.R
import com.timerx.domain.timeFormatted
import com.timerx.widget.TimerXWidget.Companion.VERTICAL_RECTANGLE

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

private fun DpSize.isThinMode(): Boolean = this == VERTICAL_RECTANGLE

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
            WidgetButtonsThin()
        } else {
            WidgetButtons()
        }
    }
}

@Composable
private fun WidgetButtons() {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Spacer(modifier = GlanceModifier.defaultWeight())
        Icon(
            modifier = GlanceModifier
                .size(48.dp),
            drawable = R.drawable.av_timer,
            action = actionStartActivity<MainActivity>(),
            contentDescription = "Home"
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        Icon(
            modifier = GlanceModifier.defaultWeight(),
            action =
            actionStartActivity<MainActivity>(
                parameters =
                actionParametersOf(
                    ActionParameters.Key<Boolean>(KEY_CREATE_TIMER) to true
                )
            ),
            drawable = R.drawable.add,
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun WidgetButtonsThin() {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Icon(
            modifier = GlanceModifier
                .size(48.dp),
            action = actionStartActivity<MainActivity>(),
            drawable = R.drawable.av_timer,
            contentDescription = "Home"
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Icon(
            modifier = GlanceModifier.defaultWeight(),
            action = actionStartActivity<MainActivity>(
                parameters =
                actionParametersOf(
                    ActionParameters.Key<Boolean>(KEY_CREATE_TIMER) to true
                )
            ),
            drawable = R.drawable.add,
        )
    }
}

@Composable
private fun Icon(
    modifier: GlanceModifier,
    action: Action,
    @DrawableRes drawable: Int,
    contentDescription: String = ""
) {
    Image(
        modifier = modifier
            .size(48.dp)
            .clickable(action),
        provider = ImageProvider(drawable),
        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
        contentDescription = contentDescription
    )
}

private const val FONT_SIZE_LARGE = 22f
private const val FONT_SIZE = 16f

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
        Column(modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()) {
            Text(
                text = timerData.name,
                style = TextStyle(
                    fontSize = TextUnit(FONT_SIZE_LARGE, TextUnitType.Sp),
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = timerData.length.timeFormatted(),
                    style = TextStyle(
                        fontSize = TextUnit(FONT_SIZE, TextUnitType.Sp),
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = timerData.lastRun,
                    style = TextStyle(
                        fontSize = TextUnit(FONT_SIZE, TextUnitType.Sp),
                        color = GlanceTheme.colors.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
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
                fontSize = TextUnit(FONT_SIZE_LARGE, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurface
            ),
            maxLines = 1
        )
        Text(
            text = timerData.length.timeFormatted(),
            style = TextStyle(
                fontSize = TextUnit(FONT_SIZE, TextUnitType.Sp),
                color = GlanceTheme.colors.onSurfaceVariant
            ),
            maxLines = 1
        )
        Text(
            text = timerData.lastRun,
            style = TextStyle(
                fontSize = TextUnit(FONT_SIZE, TextUnitType.Sp),
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
    colorProvider: ColorProvider = GlanceTheme.colors.outline,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier.fillMaxWidth()
            .height(thickness)
            .background(colorProvider = colorProvider)
    ) {}
}
