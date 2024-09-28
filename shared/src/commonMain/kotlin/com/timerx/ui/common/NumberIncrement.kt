package com.timerx.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.minus

@Composable
fun NumberIncrement(
    value: Long,
    modifier: Modifier = Modifier,
    negativeButtonEnabled: Boolean = true,
    positiveButtonEnabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = LocalTextStyle.current,
    formatter: (Long) -> String = { "$it" },
    onChange: (Long) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {},
            enabled = negativeButtonEnabled
        ) {
            Icon(
                modifier = Modifier.size(CustomIcons.defaultIconSize)
                    .repeatingClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = true,
                        maxDelayMillis = 500,
                        minDelayMillis = 100,
                        delayDecayFactor = .4f
                    ) {
                        if (negativeButtonEnabled) {
                            onChange(value - 1)
                        }
                    },
                imageVector = CustomIcons.checkIntermediateSmall,
                contentDescription = stringResource(Res.string.minus),
                tint = color
            )
        }
        AnimatedNumber(
            value = formatter(value),
            color = color,
            textStyle = textStyle
        )
        IconButton(
            onClick = {},
            enabled = positiveButtonEnabled
        ) {
            Icon(
                modifier = Modifier.size(CustomIcons.defaultIconSize)
                    .repeatingClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = true,
                        maxDelayMillis = 500,
                        minDelayMillis = 50,
                        delayDecayFactor = .5f
                    ) {
                        if (positiveButtonEnabled) {
                            onChange(value + 1)
                        }
                    },
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add),
                tint = color
            )
        }
    }
}

@Composable
fun AnimatedNumber(
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val numberWidth = rememberTextWidth(textStyle)
    Row(
        modifier = modifier.animateContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        value.forEach { char ->
            when {
                char.isDigit() -> {
                    AnimatedContent(
                        targetState = char.digitToInt(),
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInVertically { -it } togetherWith  slideOutVertically { it }
                            } else {
                                slideInVertically { it } togetherWith  slideOutVertically { -it }
                            }
                        }
                    ) { digit ->
                        Text(
                            modifier = Modifier.width(numberWidth),
                            text = "${digit.digitToChar()}",
                            style = textStyle,
                            textAlign = TextAlign.Center,
                            color = color
                        )
                    }
                }

                else -> {
                    Text(
                        text = "$char",
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun rememberTextWidth(textStyle: TextStyle, toMeasure: String = "0123456789"): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(textStyle, toMeasure) {
        val textLayoutResult: TextLayoutResult =
            textMeasurer.measure(
                text = AnnotatedString(toMeasure),
                style = textStyle
            )
        val textSize = textLayoutResult.size
        with(density) { (textSize.width / toMeasure.length).toDp() }
    }
}

fun Modifier.repeatingClickable(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
    maxDelayMillis: Long = 1000,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = .20f,
    onClick: () -> Unit
): Modifier = composed {
    val currentClickListener by rememberUpdatedState(onClick)
    val coroutineScope = rememberCoroutineScope()

    pointerInput(interactionSource, enabled) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val heldButtonJob = coroutineScope.launch {
                var currentDelayMillis = maxDelayMillis
                while (enabled && down.pressed) {
                    currentClickListener()
                    delay(currentDelayMillis)
                    val nextMillis =
                        currentDelayMillis - currentDelayMillis * delayDecayFactor
                    currentDelayMillis = nextMillis.toLong().coerceAtLeast(minDelayMillis)
                }
            }
            waitForUpOrCancellation()
            heldButtonJob.cancel()
        }
    }
}
