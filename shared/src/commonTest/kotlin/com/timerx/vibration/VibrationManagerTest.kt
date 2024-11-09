package com.timerx.vibration

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettings
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.VibrationSetting.CanVibrate
import com.timerx.sound.Beep.Alert
import com.timerx.sound.IntervalSound
import com.timerx.testutil.idle
import com.timerx.testutil.testDispatchers
import com.timerx.timermanager.RunState
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerEvent.Destroy
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.Paused
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Resumed
import com.timerx.timermanager.TimerManager
import com.timerx.vibration.Vibration.Medium
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

private class TestVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers
) :
    AbstractVibrationManager(alertSettingsManager, timerManager, txDispatchers) {
    val vibrationList = mutableListOf<Vibration>()
    override suspend fun vibrate(vibration: Vibration) {
        vibrationList.add(vibration)
    }
}

class VibrationManagerTest : FreeSpec({
    val alertSettingsManager = mock<AlertSettingsManager>()
    val timerManager = mock<TimerManager>()
    val tDispatchers = testDispatchers()

    val vibrationManagerFactory: () -> TestVibrationManager = {
        TestVibrationManager(alertSettingsManager, timerManager, tDispatchers)
    }

    val timerEventFlow = MutableStateFlow<TimerEvent>(Destroy())
    every { timerManager.eventState } returns timerEventFlow

    beforeTest {
        every { alertSettingsManager.alertSettings } returns flowOf(AlertSettings())
        every { timerManager.eventState } returns timerEventFlow
    }

    afterTest {
        resetAnswers(alertSettingsManager, timerManager)
        timerEventFlow.value = Destroy()
    }

    "default" {
        vibrationManagerFactory().isVibrationEnabled shouldBe false
    }

    "can vibrate with settings" {
        every { alertSettingsManager.alertSettings } returns flowOf(
            AlertSettings(vibrationSetting = CanVibrate(true))
        )

        val vibrationManager = vibrationManagerFactory()
        tDispatchers.main.testCoroutineScheduler.advanceUntilIdle()
        vibrationManager.isVibrationEnabled shouldBe true
    }

    "timer manager event" - {
        "disabled vibration does not invoked vibrate" {
            every { timerManager.eventState } returns MutableStateFlow(
                NextInterval(
                    runState = RunState(),
                    intervalSound = IntervalSound(Alert),
                    vibration = Medium
                )
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.main.testCoroutineScheduler.advanceUntilIdle()
            vibrationManager.vibrationList shouldBe emptyList()
        }
        "next interval invokes vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.main.testCoroutineScheduler.advanceUntilIdle()
            timerEventFlow.emit(
                NextInterval(
                    runState = RunState(),
                    intervalSound = IntervalSound(Alert),
                    vibration = Medium
                )
            )
            tDispatchers.main.testCoroutineScheduler.advanceUntilIdle()
            vibrationManager.vibrationList shouldBe listOf(Medium)
        }

        "ticker invokes vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                TimerEvent.Ticker(
                    runState = RunState(),
                    beep = Alert,
                    vibration = Vibration.Soft
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf(Vibration.Soft)
        }

        "ticker with no vibration does not invoke vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                TimerEvent.Ticker(
                    runState = RunState(),
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe emptyList()
        }

        "finished invokes vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                TimerEvent.Finished(
                    runState = RunState(),
                    intervalSound = IntervalSound(Alert, "Finished"),
                    vibration = Vibration.SoftX2
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf(Vibration.SoftX2)
        }
        "previous interval invokes vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                PreviousInterval(
                    runState = RunState(),
                    intervalSound = IntervalSound(Alert, "Finished"),
                    vibration = Vibration.SoftX3
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf(Vibration.SoftX3)
        }
        "started invokes vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                TimerEvent.Started(
                    runState = RunState(),
                    intervalSound = IntervalSound(Alert, "Finished"),
                    vibration = Medium
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf(Medium)
        }
        "destroy does not invoke vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(
                Destroy(
                    runState = RunState(),
                )
            )
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf()
        }
        "paused does not invoke vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(Paused(runState = RunState()))
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf()
        }
        "resumed does not invoke vibrate" {
            every { alertSettingsManager.alertSettings } returns flowOf(
                AlertSettings(vibrationSetting = CanVibrate(true))
            )
            val vibrationManager = vibrationManagerFactory()
            tDispatchers.idle()
            timerEventFlow.emit(Resumed(runState = RunState()))
            tDispatchers.idle()
            vibrationManager.vibrationList shouldBe listOf()
        }
    }
})
