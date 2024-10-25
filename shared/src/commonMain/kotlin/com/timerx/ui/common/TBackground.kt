package com.timerx.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.timerx.settings.BackgroundSettings
import com.timerx.settings.Pattern
import com.timerx.settings.TimerXSettings
import com.timerx.ui.theme.Animation
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.bubbles
import timerx.shared.generated.resources.rectangles
import timerx.shared.generated.resources.triangles

@Composable
fun TBackground(modifier: Modifier = Modifier) {
    val backgroundSettings by koinInject<TimerXSettings>()
        .backgroundSettingsManager
        .backgroundSettings
        .collectAsState(BackgroundSettings())

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val brush = remember(primary, tertiary) {
        Brush.verticalGradient(
            colors = listOf(
                primary, tertiary
            )
        )
    }

    AnimatedContent(
        targetState = backgroundSettings.pattern,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = Animation.fast)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = Animation.fast))
        },
    ) {
        Image(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer(alpha = backgroundSettings.backgroundAlpha.value)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                    }
                },
            painter = painterResource(backgroundSettings.pattern.drawable()),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}

private fun Pattern.drawable(): DrawableResource =
    when (this) {
        Pattern.Bubbles -> Res.drawable.bubbles
        Pattern.Rectangles -> Res.drawable.rectangles
        Pattern.Triangles -> Res.drawable.triangles
    }
