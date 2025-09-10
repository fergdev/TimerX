package com.intervallum.timermanager

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.intervallum.database.ITimerRepository
import com.intervallum.domain.interval
import com.intervallum.domain.timer
import com.intervallum.domain.timerSet
import com.intervallum.sound.Beep
import com.intervallum.sound.IntervalSound
import com.intervallum.testutil.asUnconfined
import com.intervallum.testutil.testDispatchers
import com.intervallum.timermanager.TimerState.Finished
import com.intervallum.vibration.Vibration
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

private fun TestScope.factoryContext(timerRepository: ITimerRepository): TimerManagerImpl {
    val txDispatchers = testDispatchers(testScheduler = testCoroutineScheduler)
    return TimerManagerImpl(
        timerRepository = timerRepository,
        txDispatchers = txDispatchers
    )
}

class TimerManagerImplTest : FreeSpec({
    asUnconfined()
    val timerRepository = mock<ITimerRepository>()
    beforeTest {
        everySuspend { timerRepository.incrementStartedCount(0L) } returns Unit
        everySuspend { timerRepository.incrementCompletedCount(0L) } returns Unit
    }
    afterTest { resetAnswers(timerRepository) }

    "start timer" - {
        "emits events" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                awaitItem() shouldBe TimerEvent.Started(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "test"),
                    vibration = Vibration.Medium
                )
            }
        }

        "increments timer start count" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                awaitItem() shouldBe TimerEvent.Started(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "test"),
                    vibration = Vibration.Medium
                )
                verifySuspend {
                    timerRepository.incrementStartedCount(0L)
                }
            }
        }

        "increments timer finished count" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                awaitItem() shouldBe TimerEvent.Started(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "test"),
                    vibration = Vibration.Medium
                )
                verifySuspend {
                    timerRepository.incrementStartedCount(0L)
                }
                awaitItem()
                verifySuspend {
                    timerRepository.incrementCompletedCount(0L)
                }
            }
        }
        "throws when attempting to play while a timer is running" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            shouldThrow<IllegalArgumentException> {
                timerManager.startTimer(timer { timerSet { interval {} } })
            }.apply { message shouldBe "Attempting to start timer while another timer is running" }
        }
        "can play another timer after on is finished" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                awaitItem() shouldBe TimerEvent.Started(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "test"),
                    vibration = Vibration.Medium
                )
                awaitItem() shouldBe TimerEvent.Finished(
                    runState = RunState(
                        timerState = Finished,
                        timerName = "test",
                        elapsed = 1L,
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Red
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "Finished"),
                    vibration = Vibration.Heavy
                )
                timerManager.startTimer(timer { timerSet { interval {} } })
                awaitItem() shouldBe TimerEvent.Started(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "test"),
                    vibration = Vibration.Medium
                )
                awaitItem() shouldBe TimerEvent.Finished(
                    runState = RunState(
                        timerState = Finished,
                        timerName = "test",
                        elapsed = 1L,
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Red
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "Finished"),
                    vibration = Vibration.Heavy
                )
            }
        }
    }
    "play pause" - {
        "throws when a timer is not running" {
            val timerManager = factoryContext(timerRepository)
            shouldThrow<IllegalArgumentException> {
                timerManager.playPause()
            }.apply { message shouldBe "Attempting to play/pause with null timer" }
        }
        "pauses timer" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                timerManager.playPause()
                awaitItem() shouldBe TimerEvent.Paused(
                    runState = RunState(
                        timerName = "test",
                        timerState = TimerState.Paused,
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    )
                )
                timerManager.destroy()
            }
        }
        "resumes timer" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                timerManager.playPause()
                awaitItem()
                timerManager.playPause()
                awaitItem() shouldBe TimerEvent.Resumed(
                    runState = RunState(
                        timerName = "test",
                        timerState = TimerState.Running,
                        setRepetitionCount = 1,
                        intervalCount = 1,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    )
                )
                timerManager.destroy()
            }
        }
        "throws when attempting to play finished timer" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                timerManager.playPause()
                awaitItem()
                timerManager.playPause()
                awaitItem()
                awaitItem()
                shouldThrow<IllegalArgumentException> {
                    timerManager.playPause()
                }.apply { message shouldBe "Attempting to play/pause with null timer" }
            }
        }
    }
    "next interval " - {
        "throws when no timer is playing" {
            val timerManager = factoryContext(timerRepository)
            shouldThrow<IllegalArgumentException> {
                timerManager.nextInterval()
            }.apply { message shouldBe "Attempting to invoke next interval with no timer playing" }
        }
        "next interval fires next interval" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(
                timer {
                    timerSet {
                        interval {}
                        interval {}
                    }
                }
            )
            timerManager.eventState.test {
                awaitItem()
                timerManager.nextInterval()
                awaitItem() shouldBe TimerEvent.NextInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 1,
                        intervalCount = 2,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, text = "test"),
                    vibration = Vibration.Medium
                )
                timerManager.destroy()
            }
        }
    }
    "previous interval" - {
        "throws when no timer is playing" {
            val timerManager = factoryContext(timerRepository)
            shouldThrow<IllegalArgumentException> {
                timerManager.previousInterval()
            }.apply { message shouldBe "Attempting to invoke previous interval with no timer playing" }
        }
        "fires previous interval" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(
                timer {
                    timerSet {
                        interval {}
                        interval {}
                    }
                }
            )
            timerManager.eventState.test {
                awaitItem()
                timerManager.nextInterval()
                awaitItem()
                timerManager.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    ),
                    intervalSound = IntervalSound(Beep.Alert, text = "test"),
                    vibration = Vibration.Medium
                )
                timerManager.destroy()
            }
        }
    }
    "destroy" - {
        "throws when no timer is playing" {
            val timerManager = factoryContext(timerRepository)
            shouldThrow<IllegalArgumentException> {
                timerManager.destroy()
            }.apply { message shouldBe "Attempting to invoke destroy with no timer playing" }
        }
        "fires event" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(
                timer {
                    timerSet {
                        interval {}
                        interval {}
                    }
                }
            )
            timerManager.eventState.test {
                awaitItem()
                timerManager.destroy()
                awaitItem() shouldBe TimerEvent.Destroy(
                    runState = RunState(
                        timerState = Finished,
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Red
                    )
                )
            }
        }
    }
    "is running" - {
        "false when no timer is playing" {
            factoryContext(timerRepository).isRunning() shouldBe false
        }
        "true when no is playing" {
            val timerManager = factoryContext(timerRepository)
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.isRunning() shouldBe true
            timerManager.destroy()
        }
    }
})
