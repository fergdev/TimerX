package com.timerx.ui.splash

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.contrastSystemBarColor
import kotlinx.coroutines.delay

@Composable
internal fun SplashContent(component: SplashComponent) {
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }
    contrastSystemBarColor(MaterialTheme.colorScheme.surface)
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        delay(1000L)
        component.finishSplash()
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize().scale(scale.value),
            imageVector = CustomIcons.avTimer,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}