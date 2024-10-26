package com.timerx.timermanager

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.sound.Beep
import com.timerx.sound.IntervalSound
import com.timerx.timermanager.TimerState.Finished
import com.timerx.timermanager.TimerState.Running
import com.timerx.util.asUnconfined
import com.timerx.util.idle
import com.timerx.vibration.Vibration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Instant

class TimerStateMachineImplTest : FreeSpec({
    asUnconfined()
    "empty timer" - {
        shouldThrow<IllegalStateException> {
            TimerStateMachineImpl(
                Timer(
                    name = "test",
                    createdAt = Instant.DISTANT_PAST,
                    sets = persistentListOf()
                ),
                coroutineScope = testScope
            )
        }.apply {
            message shouldBe "Timer must have at least one set"
        }
    }
    "single empty set" - {
        shouldThrow<IllegalStateException> {
            TimerStateMachineImpl(
                Timer(
                    name = "test",
                    createdAt = Instant.DISTANT_PAST,
                    sets = persistentListOf(
                        TimerSet(
                            0, repetitions = 1, intervals = persistentListOf()
                        )
                    )
                ),
                coroutineScope = testScope
            )
        }.apply {
            idle()
            message shouldBe "Timer set must have at least one interval"
        }
    }
    "single interval one second" - {
        TimerStateMachineImpl(
            Timer(
                name = "test",
                createdAt = Instant.DISTANT_PAST,
                sets = persistentListOf(
                    TimerSet(
                        0, repetitions = 1, intervals = persistentListOf(
                            TimerInterval(
                                name = "work",
                                duration = 1L
                            )
                        )
                    )
                )
            ),
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
                    intervalDuration = 1,
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
            Timer(
                name = "test",
                createdAt = Instant.DISTANT_PAST,
                sets = persistentListOf(
                    TimerSet(
                        0, repetitions = 1, intervals = persistentListOf(
                            TimerInterval(
                                name = "work",
                                duration = 10L
                            )
                        )
                    )
                )
            ),
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
})
