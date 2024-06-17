package com.timerx.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.timerx.ui.CustomIcons
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.minus

@Composable
fun NumberIncrement(
    modifier: Modifier = Modifier,
    value: Int,
    formatter: (Int) -> String = { "$it" },
    negativeButtonEnabled: Boolean = true,
    positiveButtonEnabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = LocalTextStyle.current,
    onChange: (Int) -> Unit,
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
                imageVector = CustomIcons.checkIndeterminateSmall(),
                contentDescription = stringResource(Res.string.minus),
                tint = color
            )
        }
        AnimatedNumber(
            value = value,
            formatter = formatter,
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
                        minDelayMillis = 100,
                        delayDecayFactor = .4f
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
    value: Int,
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    formatter: (Int) -> String
) {
    AnimatedContent(targetState = value, transitionSpec = {
        if (targetState > initialState) {
            slideInVertically { -it } togetherWith slideOutVertically { it }
        } else {
            slideInVertically { it } togetherWith slideOutVertically { -it }
        }
    }) { count ->
        Text(
            text = formatter(count),
            style = textStyle,
            color = color
        )
    }
}

fun Modifier.repeatingClickable(
    interactionSource: InteractionSource,
    enabled: Boolean,
    maxDelayMillis: Long = 1000,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = .20f,
    onClick: () -> Unit
): Modifier = composed {

    val currentClickListener by rememberUpdatedState(onClick)

    pointerInput(interactionSource, enabled) {
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val heldButtonJob = launch {
                        var currentDelayMillis = maxDelayMillis
                        while (enabled && down.pressed) {
                            currentClickListener()
                            delay(currentDelayMillis)
                            val nextMillis =
                                currentDelayMillis - (currentDelayMillis * delayDecayFactor)
                            currentDelayMillis = nextMillis.toLong().coerceAtLeast(minDelayMillis)
                        }
                    }
                    waitForUpOrCancellation()
                    heldButtonJob.cancel()
                }
            }
        }
    }
}
