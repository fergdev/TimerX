package com.timerx.sound

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettings
import com.timerx.settings.AlertSettingsManager
import com.timerx.sound.Beep.Alert
import com.timerx.testutil.idle
import com.timerx.testutil.testDispatchers
import com.timerx.timermanager.RunState
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerEvent.Destroy
import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.Paused
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Resumed
import com.timerx.timermanager.TimerEvent.Started
import com.timerx.timermanager.TimerManager
import com.timerx.vibration.Vibration
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

private class TestSoundManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers,
    override val isTTSSupported: Boolean = true
) : AbstractSoundManager(alertSettingsManager, timerManager, txDispatchers) {

    val beeps = mutableListOf<Beep>()
    val textToSpeech = mutableListOf<String>()

    override suspend fun beep(beep: Beep) {
        beeps.add(beep)
    }

    override suspend fun textToSpeech(text: String) {
        textToSpeech.add(text)
    }

    override fun voices(): List<VoiceInformation> = testVoices
}

class AbstractSoundManagerTest : FreeSpec({
    val alertSettingsManager = mock<AlertSettingsManager>()
    val timerEventFlow = MutableStateFlow<TimerEvent>(Destroy())
    val timerManager = mock<TimerManager>()
    val tDispatchers = testDispatchers()
    val factory: () -> TestSoundManager = {
        TestSoundManager(alertSettingsManager, timerManager, tDispatchers)
    }

    beforeTest {
        every { timerManager.eventState } returns timerEventFlow
        every { alertSettingsManager.alertSettings } returns flowOf(AlertSettings(Volume(0.8f)))
    }
    "init" - {
        "default" {
            val soundManager = factory()
            soundManager.volume shouldBe Volume.default
            soundManager.voices() shouldBe testVoices
            soundManager.beeps shouldBe emptyList()
            soundManager.textToSpeech shouldBe emptyList()
            soundManager.isTTSSupported shouldBe true
        }

        "collects volume" {
            val soundManager = factory()
            tDispatchers.idle()
            soundManager.volume shouldBe Volume(0.8f)
        }
    }
    "timer event" - {
        "ticker" - {
            "plays beep" {
                val soundManager = factory()
                timerEventFlow.emit(TimerEvent.Ticker(RunState(), beep = Alert))
                tDispatchers.idle()
                soundManager.beeps shouldBe listOf(Alert)
            }
            "does not play beep" {
                val soundManager = factory()
                timerEventFlow.emit(TimerEvent.Ticker(RunState()))
                tDispatchers.idle()
                soundManager.beeps shouldBe emptyList()
            }
        }
        "finished" - {
            "plays beep" {
                val soundManager = factory()
                timerEventFlow.emit(
                    Finished(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, null),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe listOf(Alert)
                soundManager.textToSpeech shouldBe emptyList()
            }
            "plays text to speech" {
                val soundManager = factory()
                timerEventFlow.emit(
                    Finished(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, "Finished"),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe emptyList()
                soundManager.textToSpeech shouldBe listOf("Finished")
            }
        }
        "next interval" - {
            "plays beep" {
                val soundManager = factory()
                timerEventFlow.emit(
                    NextInterval(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, null),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe listOf(Alert)
                soundManager.textToSpeech shouldBe emptyList()
            }
            "plays text to speech" {
                val soundManager = factory()
                timerEventFlow.emit(
                    NextInterval(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, "next"),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe emptyList()
                soundManager.textToSpeech shouldBe listOf("next")
            }
        }
        "previous interval" - {
            "plays beep" {
                val soundManager = factory()
                timerEventFlow.emit(
                    PreviousInterval(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, null),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe listOf(Alert)
                soundManager.textToSpeech shouldBe emptyList()
            }
            "plays text to speech" {
                val soundManager = factory()
                timerEventFlow.emit(
                    PreviousInterval(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, "previous"),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe emptyList()
                soundManager.textToSpeech shouldBe listOf("previous")
            }
        }
        "started" - {
            "plays beep" {
                val soundManager = factory()
                timerEventFlow.emit(
                    Started(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, null),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe listOf(Alert)
                soundManager.textToSpeech shouldBe emptyList()
            }
            "plays text to speech" {
                val soundManager = factory()
                timerEventFlow.emit(
                    Started(
                        runState = RunState(),
                        intervalSound = IntervalSound(Alert, "started"),
                        vibration = Vibration.Soft
                    )
                )
                tDispatchers.idle()
                soundManager.beeps shouldBe emptyList()
                soundManager.textToSpeech shouldBe listOf("started")
            }
        }
        "destroy does nothing" {
            val soundManager = factory()
            timerEventFlow.emit(Destroy(runState = RunState()))
            tDispatchers.idle()
            soundManager.beeps shouldBe emptyList()
            soundManager.textToSpeech shouldBe emptyList()
        }
        "paused does nothing" {
            val soundManager = factory()
            timerEventFlow.emit(Paused(runState = RunState()))
            tDispatchers.idle()
            soundManager.beeps shouldBe emptyList()
            soundManager.textToSpeech shouldBe emptyList()
        }
        "resumed does nothing" {
            val soundManager = factory()
            timerEventFlow.emit(Resumed(runState = RunState()))
            tDispatchers.idle()
            soundManager.beeps shouldBe emptyList()
            soundManager.textToSpeech shouldBe emptyList()
        }
    }
})
