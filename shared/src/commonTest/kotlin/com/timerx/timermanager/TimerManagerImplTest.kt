package com.timerx.timermanager

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.timerx.database.ITimerRepository
import com.timerx.domain.interval
import com.timerx.domain.timer
import com.timerx.domain.timerSet
import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.util.asUnconfined
import com.timerx.vibration.Vibration
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

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
            val timerManager = TimerManagerImpl(
                timerRepository = timerRepository,
                coroutineScope = testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository = timerRepository,
                coroutineScope = testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            timerManager.startTimer(timer { timerSet { interval {} } })
            shouldThrow<IllegalArgumentException> {
                timerManager.startTimer(timer { timerSet { interval {} } })
            }.apply { message shouldBe "Attempting to start timer while another timer is running" }
        }
    }
    "play pause" - {
        "throws when a timer is not running" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            shouldThrow<IllegalArgumentException> {
                timerManager.playPause()
            }.apply { message shouldBe "Attempting to play/pause with null timer" }
        }
        "pauses timer" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.eventState.test {
                awaitItem()
                timerManager.playPause()
                awaitItem()
                timerManager.playPause()
                awaitItem()
                awaitItem()
                shouldThrow<IllegalStateException> {
                    timerManager.playPause()
                }.apply { message shouldBe "Cannot play/pause finished timer" }
                timerManager.destroy()
            }
        }
    }
    "next interval " - {
        "throws when no timer is playing" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            shouldThrow<IllegalArgumentException> {
                timerManager.nextInterval()
            }.apply { message shouldBe "Attempting to invoke next interval with no timer playing" }
        }
        "next interval fires next interval" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            shouldThrow<IllegalArgumentException> {
                timerManager.previousInterval()
            }.apply { message shouldBe "Attempting to invoke previous interval with no timer playing" }
        }
        "fires previous interval" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            shouldThrow<IllegalArgumentException> {
                timerManager.destroy()
            }.apply { message shouldBe "Attempting to invoke destroy with no timer playing" }
        }
        "fires event" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
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
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "test",
                        intervalDuration = 1L,
                        backgroundColor = Color.Blue
                    )
                )
            }
        }
    }
    "is running" - {
        "false when no timer is playing" {
            TimerManagerImpl(
                timerRepository,
                testScope
            ).isRunning() shouldBe false
        }
        "true when no is playing" {
            val timerManager = TimerManagerImpl(
                timerRepository,
                testScope
            )
            timerManager.startTimer(timer { timerSet { interval {} } })
            timerManager.isRunning() shouldBe true
            timerManager.destroy()
        }
    }
})
