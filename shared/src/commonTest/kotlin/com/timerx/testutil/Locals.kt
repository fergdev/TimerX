package com.timerx.testutil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.timerx.analytics.TimerXAnalytics
import com.timerx.ui.logging.LocalTimerXAnalytics
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock

object TestLifecycleOwner : LifecycleOwner {
    override val lifecycle: Lifecycle
        get() = object : Lifecycle() {
            override val currentState: State
                get() = State.RESUMED

            override fun addObserver(observer: LifecycleObserver) {
                // nothing
            }

            override fun removeObserver(observer: LifecycleObserver) {
                // nothing
            }
        }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.setContentWithLocals(content: @Composable () -> Unit) {
    val timerXAnalytics = mock<TimerXAnalytics> {
        every { logScreen(any()) } returns Unit
        every { logException(any()) } returns Unit
        every { logEvent(any(), any()) } returns Unit
    }
    setContent {
        CompositionLocalProvider(LocalLifecycleOwner provides TestLifecycleOwner) {
            CompositionLocalProvider(LocalTimerXAnalytics provides timerXAnalytics) {
                content()
            }
        }
    }
}
