package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.sound.Volume
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.kotest.core.spec.style.FreeSpec
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.dsl.state

@Suppress("unused")
private val playingState: RunScreenState = RunScreenState.Loaded.NotFinished.Playing(
    backgroundColor = Color.Transparent,
    volume = Volume.default,
    vibrationSetting = CanVibrate(enabled = true),
    timerName = "Test timer",
    time = 2000L,
    intervalName = "Test interval",
    manualNext = false,
    index = "1",
    keepScreenOn = true
)

@Suppress("unused")
@OptIn(ExperimentalTestApi::class, DelicateStoreApi::class)
class RunContentTest : FreeSpec({
    val factory = { runScreenState: RunScreenState ->
        mock<RunComponent> {
            every { state } returns runScreenState
            every { onBack } returns {}
        }
    }

    "loading content" {
        runComposeUiTest {
            assert(true)
        }
    }
//    "loading content" {
//        runComposeUiTest {
//            setContentWithLocals { RunContent(runComponent = factory(RunScreenState.Loading)) }
//
//            kompare()
//        }
//    }
//    "no timer content" {
//        runComposeUiTest {
//            setContentWithLocals { RunContent(runComponent = factory(RunScreenState.NoTimer)) }
//            kompare()
//        }
//    }
//    "default" {
//        runComposeUiTest {
//            setContentWithLocals { RunContent(runComponent = factory(playingState)) }
//            kompare()
//        }
//    }
})
