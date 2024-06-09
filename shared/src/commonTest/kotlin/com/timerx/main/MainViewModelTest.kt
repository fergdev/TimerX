package com.timerx.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.ui.main.MainViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private val timers = mutableListOf(
        Timer("1", "name 1", persistentListOf()),
        Timer("2", "name 2", persistentListOf()),
    )

    private var timerRepository: ITimerRepository = object : ITimerRepository {
        override fun getTimers(): List<Timer> {
            return timers
        }

        override fun insertTimer(timer: Timer) {
        }

        override fun updateTimer(timer: Timer) {
        }

        override fun deleteTimer(timer: Timer) {
        }

        override fun duplicate(timer: Timer) {
            timers.add(timer.copy(id = "${timers.size + 1}"))
        }

        override fun getTimer(timerId: String): Timer {
            return timers.first { it.id == timerId }
        }
    }

    @BeforeTest
    fun setup() {
        viewModel = MainViewModel(timerRepository)
    }

    @Test
    fun `init - sets state`() {
        // given  + when + then
        assertEquals(2, viewModel.state.value.timers.size)
    }

    @Test
    fun `refresh timers - updates state`() {
        // given
        timers.add(Timer("3", "name 3", persistentListOf()))

        // when
        viewModel.refreshData()

        // then
        assertEquals(3, viewModel.state.value.timers.size)
    }

    @Test
    fun `delete timer - deletes timer`() {
        // given + when
        viewModel.deleteTimer(timers[1])

        // then
        assertEquals(2, viewModel.state.value.timers.size)
    }

    @Test
    fun `duplicate timer - updates state`() {
        // given + when
        viewModel.duplicateTimer(timers[1])

        // then
        assertEquals(3, viewModel.state.value.timers.size)
    }
}
