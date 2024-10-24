package com.timerx.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.timerx.ui.theme.Opacity
import com.timerx.ui.theme.Size

@Composable
fun TIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = Size.icon,
    tint: Color = Color.Unspecified,
    alpha: Float = Opacity.enabled,
) {
    Image(
        imageVector = imageVector,
        modifier = modifier.requiredSizeIn(maxWidth = size, maxHeight = size).testTag(imageVector.name),
        contentDescription = contentDescription,
        alpha = alpha,
        colorFilter = ColorFilter.tint(tint.takeOrElse { LocalContentColor.current }),
    )
}

@Composable
fun TIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = Size.icon,
    tint: Color = Color.Unspecified,
    enabled: Boolean = true,
    enabledAlpha: Float = 1f,
) {
    val alpha by animateFloatAsState(if (enabled) enabledAlpha else Opacity.disabled)
    Box(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(radius = size / 2),
            )
            .minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center,
    ) {
        TIcon(
            imageVector = icon,
            size = size,
            tint = tint,
            alpha = alpha,
            contentDescription = contentDescription
        )
    }
}
