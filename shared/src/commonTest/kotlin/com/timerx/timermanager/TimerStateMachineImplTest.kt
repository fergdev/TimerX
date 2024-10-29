package com.timerx.timermanager

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.timerx.domain.FinalCountDown
import com.timerx.domain.interval
import com.timerx.domain.timer
import com.timerx.domain.timerSet
import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.testutil.asUnconfined
import com.timerx.timermanager.TimerEvent.Destroy
import com.timerx.timermanager.TimerState.Finished
import com.timerx.timermanager.TimerState.Paused
import com.timerx.timermanager.TimerState.Running
import com.timerx.vibration.Vibration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

@Suppress("LargeClass")
class TimerStateMachineImplTest : FreeSpec({
    asUnconfined()
    "single interval one second" - {
        TimerStateMachineImpl(
            timer {
                timerSet {
                    interval {
                        name = "work"
                        duration = 1L
                    }
                }
            },
            coroutineScope = testScope
        ).eventState.test {
            awaitItem() shouldBe TimerEvent.Started(
                runState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 1,
                    intervalName = "work",
                    intervalDuration = 1L,
                    backgroundColor = Color.Blue
                ),
                intervalSound = IntervalSound(Beep.Alert, "work"),
                vibration = Vibration.Medium
            )
            awaitItem() shouldBe TimerEvent.Finished(
                runState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 1,
                    intervalName = "work",
                    intervalDuration = 1L,
                    backgroundColor = Color.Red,
                    timerState = Finished,
                    elapsed = 1
                ),
                intervalSound = IntervalSound(Beep.Alert, "Finished"),
                vibration = Vibration.Heavy
            )
        }
    }
    "single interval 10 seconds" - {
        TimerStateMachineImpl(
            timer {
                timerSet {
                    interval {
                        name = "work"
                        duration = 10L
                    }
                }
            },
            coroutineScope = testScope
        ).eventState.test {
            val runState = RunState(
                timerName = "test",
                setRepetitionCount = 1,
                intervalCount = 1,
                intervalName = "work",
                intervalDuration = 10L,
                backgroundColor = Color.Blue,
                timerState = Running,
            )
            awaitItem() shouldBe TimerEvent.Started(
                runState = runState,
                intervalSound = IntervalSound(Beep.Alert, "work"),
                vibration = Vibration.Medium
            )
            for (elapsed in 1L..6L) {
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = runState.copy(elapsed = elapsed),
                )
            }
            for (i in 7L..9L) {
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = runState.copy(
                        elapsed = i
                    ),
                    beep = Beep.Alert,
                    vibration = Vibration.Light
                )
            }
            awaitItem() shouldBe TimerEvent.Finished(
                runState = runState.copy(
                    backgroundColor = Color.Red,
                    timerState = Finished,
                    elapsed = 10L
                ),
                intervalSound = IntervalSound(Beep.Alert, "Finished"),
                vibration = Vibration.Heavy
            )
        }
    }
    "two intervals 10 seconds" - {
        TimerStateMachineImpl(
            timer {
                timerSet {
                    interval {
                        name = "work"
                        duration = 10L
                    }
                    interval {
                        name = "rest"
                        duration = 20L
                        color = Color.Green
                        countUp = true
                        textToSpeech = false
                        beep = Beep.Whistle
                        vibration = Vibration.Soft
                        finalCountDown = FinalCountDown(
                            duration = 1,
                            beep = Beep.Alert2,
                            vibration = Vibration.Light
                        )
                    }
                }
            },
            coroutineScope = testScope
        ).eventState.test {
            val workRunState = RunState(
                timerName = "test",
                setRepetitionCount = 1,
                intervalCount = 2,
                intervalName = "work",
                intervalDuration = 10L,
                backgroundColor = Color.Blue,
                timerState = Running,
            )
            awaitItem() shouldBe TimerEvent.Started(
                runState = workRunState,
                intervalSound = IntervalSound(Beep.Alert, "work"),
                vibration = Vibration.Medium
            )
            for (elapsed in 1L..6L) {
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = workRunState.copy(elapsed = elapsed),
                )
            }
            for (i in 7L..9L) {
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = workRunState.copy(elapsed = i),
                    beep = Beep.Alert,
                    vibration = Vibration.Light
                )
            }
            val restRunState = RunState(
                timerName = "test",
                setRepetitionCount = 1,
                intervalIndex = 1,
                intervalCount = 2,
                intervalName = "rest",
                intervalDuration = 20L,
                backgroundColor = Color.Green,
                displayCountAsUp = true,
                timerState = Running,
            )
            awaitItem() shouldBe TimerEvent.NextInterval(
                runState = restRunState,
                intervalSound = IntervalSound(Beep.Whistle),
                vibration = Vibration.Soft
            )
            for (elapsed in 1L..18L) {
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = restRunState.copy(elapsed = elapsed),
                )
            }
            awaitItem() shouldBe TimerEvent.Ticker(
                runState = restRunState.copy(elapsed = 19L),
                beep = Beep.Alert2,
                vibration = Vibration.Light
            )
            awaitItem() shouldBe TimerEvent.Finished(
                runState = restRunState.copy(
                    backgroundColor = Color.Red,
                    timerState = Finished,
                    elapsed = 20L
                ),
                intervalSound = IntervalSound(Beep.Alert, "Finished"),
                vibration = Vibration.Heavy
            )
        }
    }
    "pause" - {
        "stops ticker" - {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                val runState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 1,
                    intervalName = "work",
                    intervalDuration = 10L,
                    backgroundColor = Color.Blue,
                    timerState = Running,
                )
                awaitItem() shouldBe TimerEvent.Started(
                    runState = runState,
                    intervalSound = IntervalSound(Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                testCoroutineScheduler.advanceTimeBy(1000)
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = runState.copy(elapsed = 1L),
                )
                timerStateMachineImpl.pause()
                expectNoEvents()
            }
        }
        "while paused throws error" - {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(5)
                timerStateMachineImpl.pause()
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.pause()
                }.apply { message shouldBe "Cannot pause while timer is Paused" }
            }
        }
        "while finished throws error" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(11)
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.pause()
                }.apply {
                    message shouldBe "Cannot pause while timer is Finished"
                }
            }
        }
    }
    "resume" - {
        "throws when running" - {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(6)
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.resume()
                }.apply { message shouldBe "Cannot resume while timer is Running" }
            }
        }
        "throws when finished" - {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(11)
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.resume()
                }.apply { message shouldBe "Cannot resume while timer is Finished" }
            }
        }
        "restarts timer" - {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                val runState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 1,
                    intervalName = "work",
                    intervalDuration = 10L,
                    backgroundColor = Color.Blue,
                    timerState = Running,
                )
                awaitItem() shouldBe TimerEvent.Started(
                    runState = runState,
                    intervalSound = IntervalSound(Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                for (elapsed in 1L..6L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = runState.copy(elapsed = elapsed),
                    )
                }
                timerStateMachineImpl.pause()
                timerStateMachineImpl.resume()
                awaitItem() shouldBe TimerEvent.Resumed(
                    runState = runState.copy(elapsed = 6L),
                )
                for (i in 7L..9L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = runState.copy(
                            elapsed = i
                        ),
                        beep = Beep.Alert,
                        vibration = Vibration.Light
                    )
                }
                awaitItem() shouldBe TimerEvent.Finished(
                    runState = runState.copy(
                        backgroundColor = Color.Red,
                        timerState = Finished,
                        elapsed = 10L
                    ),
                    intervalSound = IntervalSound(Beep.Alert, "Finished"),
                    vibration = Vibration.Heavy
                )
            }
        }
    }
    "next interval" - {
        "advances to next interval" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(5)
                timerStateMachineImpl.nextInterval()
                awaitItem() shouldBe TimerEvent.NextInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 1,
                        intervalCount = 2,
                        intervalName = "rest",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "rest"),
                    vibration = Vibration.Medium
                )
            }
        }
        "advances to next interval while paused" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(5)
                timerStateMachineImpl.pause()
                timerStateMachineImpl.nextInterval()
                awaitItem() shouldBe TimerEvent.NextInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 1,
                        intervalCount = 2,
                        intervalName = "rest",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Paused,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "rest"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "advances to finish with skip on last set" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                            skipOnLastSet = true
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(5)
                timerStateMachineImpl.nextInterval()
                awaitItem() shouldBe TimerEvent.Finished(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        elapsed = 4L,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Red,
                        timerState = Finished,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "Finished"),
                    vibration = Vibration.Heavy
                )
                expectNoEvents()
            }
        }
        "throws when finished" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(11)
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.nextInterval()
                }.apply { message shouldBe "Cannot skip while timer is Finished" }
            }
        }
    }
    "previous interval" - {
        "resets to start of interval" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(5)
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 1,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
            }
        }
        "goes to previous interval" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.nextInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
            }
        }
        "goes to previous interval while paused" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.pause()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Paused,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "does nothing when at the start of the timer" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.pause()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                expectNoEvents()
            }
        }
        "goes to previous interval after one repetition of set" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        repetitions = 2
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.nextInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 2,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "goes to previous set" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                    timerSet {
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.nextInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 1,
                        intervalIndex = 0,
                        intervalCount = 1,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "goes all the way back to start of timer" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        repetitions = 2
                        interval {
                            name = "work"
                            duration = 10L
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                awaitItem()
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.nextInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        setRepetitionCount = 2,
                        intervalIndex = 0,
                        intervalCount = 2,
                        intervalName = "work",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "handles skip on last set" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        repetitions = 3
                        interval {
                            name = "work"
                            duration = 10L
                            skipOnLastSet = true
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.nextInterval()
                timerStateMachineImpl.nextInterval()
                awaitItem()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        repetitionIndex = 1,
                        setRepetitionCount = 3,
                        intervalIndex = 1,
                        intervalCount = 2,
                        intervalName = "rest",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "rest"),
                    vibration = Vibration.Medium
                )
                timerStateMachineImpl.previousInterval()
                timerStateMachineImpl.previousInterval()
                awaitItem() shouldBe TimerEvent.PreviousInterval(
                    runState = RunState(
                        timerName = "test",
                        repetitionIndex = 0,
                        setRepetitionCount = 3,
                        intervalIndex = 1,
                        intervalCount = 2,
                        intervalName = "rest",
                        intervalDuration = 10L,
                        backgroundColor = Color.Blue,
                        timerState = Running,
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "rest"),
                    vibration = Vibration.Medium
                )
                expectNoEvents()
            }
        }
        "throws when finished" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                skipItems(11)
                shouldThrow<IllegalArgumentException> {
                    timerStateMachineImpl.previousInterval()
                }.apply { message shouldBe "Cannot skip back while timer is Finished" }
            }
        }
    }
    "manual next" - {
        "pauses timer" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                            manualNext = true
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                val workRunState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 2,
                    intervalName = "work",
                    intervalDuration = 10L,
                    backgroundColor = Color.Blue,
                    timerState = Running,
                    manualNext = true
                )
                awaitItem() shouldBe TimerEvent.Started(
                    runState = workRunState,
                    intervalSound = IntervalSound(Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                for (elapsed in 1L..6L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = workRunState.copy(elapsed = elapsed),
                    )
                }
                for (i in 7L..9L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = workRunState.copy(elapsed = i),
                        beep = Beep.Alert,
                        vibration = Vibration.Light
                    )
                }
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = workRunState.copy(elapsed = 10L),
                    beep = null,
                    vibration = null
                )
                expectNoEvents()
            }
        }
        "next interval resumes after manual next pause" {
            val timerStateMachineImpl = TimerStateMachineImpl(
                timer {
                    timerSet {
                        interval {
                            name = "work"
                            duration = 10L
                            manualNext = true
                        }
                        interval {
                            name = "rest"
                            duration = 10L
                        }
                    }
                },
                coroutineScope = testScope
            )
            timerStateMachineImpl.eventState.test {
                val workRunState = RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 2,
                    intervalName = "work",
                    intervalDuration = 10L,
                    backgroundColor = Color.Blue,
                    timerState = Running,
                    manualNext = true
                )
                awaitItem() shouldBe TimerEvent.Started(
                    runState = workRunState,
                    intervalSound = IntervalSound(Beep.Alert, "work"),
                    vibration = Vibration.Medium
                )
                for (elapsed in 1L..6L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = workRunState.copy(elapsed = elapsed),
                    )
                }
                for (i in 7L..9L) {
                    awaitItem() shouldBe TimerEvent.Ticker(
                        runState = workRunState.copy(elapsed = i),
                        beep = Beep.Alert,
                        vibration = Vibration.Light
                    )
                }
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = workRunState.copy(elapsed = 10L),
                    beep = null,
                    vibration = null
                )
                expectNoEvents()
                timerStateMachineImpl.nextInterval()
                awaitItem() shouldBe TimerEvent.NextInterval(
                    runState = workRunState.copy(
                        intervalIndex = 1,
                        intervalName = "rest",
                        elapsed = 0L,
                        manualNext = false
                    ),
                    intervalSound = IntervalSound(beep = Beep.Alert, "rest"),
                    vibration = Vibration.Medium
                )
                awaitItem() shouldBe TimerEvent.Ticker(
                    runState = workRunState.copy(
                        elapsed = 1L,
                        intervalIndex = 1,
                        intervalName = "rest",
                        manualNext = false
                    )
                )
            }
        }
    }
    "destroy emits destroy event" {
        val timerStateMachineImpl = TimerStateMachineImpl(
            timer {
                timerSet {
                    interval {
                        name = "work"
                        duration = 10L
                    }
                }
            },
            coroutineScope = testScope
        )
        timerStateMachineImpl.eventState.test {
            timerStateMachineImpl.destroy()
            awaitItem()
            awaitItem() shouldBe Destroy(
                RunState(
                    timerName = "test",
                    setRepetitionCount = 1,
                    intervalCount = 1,
                    intervalName = "work",
                    intervalDuration = 10L,
                    backgroundColor = Color.Blue,
                )
            )
        }
    }
})
