package com.intervallum.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val IconSizeMultiplier = 0.6f

@Composable
fun TCircleIcon(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    color: Color = rememberRandomColor(),
    elevation: Dp = 0.dp,
    size: Dp = 40.dp,
    backdropOpacity: Float = 0.17f,
    iconSize: Dp = size * IconSizeMultiplier,
) {
    val animatedColor by animateColorAsState(color)

    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = animatedColor.copy(alpha = backdropOpacity),
        contentColor = animatedColor,
        shadowElevation = elevation,
        tonalElevation = 0.dp,
        border = null,
        content = {
            AnimatedContent(icon) {
                TIcon(
                    imageVector = it,
                    tint = animatedColor,
                    size = iconSize,
                    contentDescription = contentDescription
                )
            }
        },
    )
}
