package com.intervallum.ui.shader

import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import kotlin.time.TimeSource

@Composable
internal fun FPSLaunchedEffect(onTimeUpdate: (Float) -> Unit) {
    LaunchedEffect(Unit) {
        val mark = TimeSource.Monotonic.markNow()
        var frameCount = 0
        var prevTime = mark.elapsedNow().inWholeNanoseconds
        var frameRate = 0
        while (true) {
            withInfiniteAnimationFrameNanos {
                frameCount++
                val inWholeNanoseconds = mark.elapsedNow().inWholeNanoseconds
                val timeDiff = inWholeNanoseconds - prevTime
                val seconds = timeDiff / 1E9 // 1E9 nanoseconds is 1 second
                if (seconds >= 1) {
                    frameRate = (frameCount / seconds).toInt()
                    prevTime = it
                    frameCount = 0
                }
                val value = inWholeNanoseconds / 1e9f
                Logger.i { "Time $value frameRate $frameRate" }
                onTimeUpdate(value)
            }
        }
    }
}
