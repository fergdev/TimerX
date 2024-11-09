package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.kompare.kompare
import com.timerx.settings.VibrationSetting
import com.timerx.sound.Volume
import com.timerx.testutil.NotAndroidCondition
import com.timerx.testutil.createComponent
import com.timerx.testutil.setContentWithLocals
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store

class FakeRunContainer(
    private val state: RunScreenState = RunScreenState.Loaded.NotFinished.Playing(
        backgroundColor = Color.Transparent,
        volume = Volume.default,
        vibrationSetting = VibrationSetting.CanVibrate(enabled = true),
        timerName = "Test timer",
        time = 2000L,
        intervalName = "Test interval",
        manualNext = false,
        index = "1",
        keepScreenOn = true
    )
) : Container<RunScreenState, RunScreenIntent, Nothing> {
    override val store: Store<RunScreenState, RunScreenIntent, Nothing>
        get() = store(state) {}
}

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@OptIn(ExperimentalTestApi::class)
@EnabledIf(NotAndroidCondition::class)
class RunContentTest : FreeSpec({
    val factory = {
        createComponent {
            DefaultRunComponent(
                it,
                { FakeRunContainer() },
                {}
            )
        }
    }

    "default" {
        runComposeUiTest {
            setContentWithLocals {
                RunContent(runComponent = factory())
            }
            kompare()
        }
    }
})
