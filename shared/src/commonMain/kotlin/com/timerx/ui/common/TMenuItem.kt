package com.timerx.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.timerx.ui.theme.Opacity
import com.timerx.util.composeLet

@Suppress("ComposableParametersOrdering") // as intended
@Composable
fun TMenuItem(
    title: (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    secondary: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    TCard(
        modifier = modifier
            .wrapContentHeight()
            .animateContentSize()
            .widthIn(max = 600.dp),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = icon != null,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                icon?.let { it() }
            }

            Column(
                modifier = Modifier.weight(1f, true),
                verticalArrangement = Arrangement.Center
            ) {
                Box(Modifier.padding(2.dp)) {
                    title()
                }
                AnimatedVisibility(secondary != null) {
                    Box(modifier = Modifier.padding(2.dp)) {
                        secondary?.let { it() }
                    }
                }
            }

            AnimatedVisibility(
                visible = trailing != null,
                modifier = Modifier.padding(2.dp),
            ) {
                trailing?.let { it() }
            }
        }
    }
}

@Composable
fun TMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = rememberRandomColor(),
    icon: ImageVector? = null,
    enabled: Boolean = true,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val textColor =
        LocalContentColor.current.copy(alpha = if (enabled) Opacity.enabled else Opacity.disabled)
    TMenuItem(
        icon = icon?.composeLet {
            TMenuItemIcon(
                imageVector = it,
                tint = if (enabled) color else LocalContentColor.current,
                contentDescription = title
            )
        },
        title = {
            TMenuItemTitle(
                text = title,
                color = textColor,
            )
        },
        modifier = modifier,
        onClick = onClick.takeIf { enabled },
        secondary = subtitle
            .takeIfValid()
            .composeLet { TMenuItemSubtitle(text = it, color = textColor) },
        trailing = trailing,
    )
}

@Composable
fun TMenuItemIcon(
    imageVector: ImageVector,
    tint: Color,
    contentDescription: String
) = TCircleIcon(
    icon = imageVector,
    color = tint,
    size = 40.dp,
    contentDescription = contentDescription
)

@Composable
fun TMenuItemSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val animatedColor by animateColorAsState(color)
    Text(
        modifier = modifier
            .padding(2.dp)
            .animateContentSize(),
        text = text,
        maxLines = 3,
        color = animatedColor,
        style = MaterialTheme.typography.bodySmall,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun TMenuItemTitle(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val animatedColor by animateColorAsState(color)
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 3,
        color = animatedColor,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(2.dp)
            .animateContentSize(),
    )
}
