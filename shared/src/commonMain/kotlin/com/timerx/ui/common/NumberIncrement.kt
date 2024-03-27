@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.timerx.ui.CustomIcons
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.minus

@Composable
fun NumberIncrement(
    value: Int,
    formatter: (Int) -> String = { "$it" },
    negativeButtonEnabled: Boolean = true,
    positiveButtonEnabled: Boolean = true,
    onChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onChange(value - 1) }, enabled = negativeButtonEnabled) {
            Icon(
                modifier = Modifier.size(CustomIcons.defaultIconSize),
                imageVector = CustomIcons.checkIndeterminateSmall(),
                contentDescription = stringResource(Res.string.minus)
            )
        }
        AnimatedNumber(value = value, formatter = formatter)
        IconButton(onClick = { onChange(value + 1) }, enabled = positiveButtonEnabled) {
            Icon(
                modifier = Modifier.size(CustomIcons.defaultIconSize),
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add)
            )
        }
    }
}

@Composable
fun AnimatedNumber(
    value: Int,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    formatter: (Int) -> String
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { -it } togetherWith slideOutVertically { it }
            } else {
                slideInVertically { it } togetherWith slideOutVertically { -it }
            }
        }
    ) { count ->
        Text(
            text = formatter(count),
            style = style,
            color = color
        )
    }
}
