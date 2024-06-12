package com.timerx.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.ui.main.MainViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals


class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private val timers = mutableListOf(
        Timer("1", "name 1", persistentListOf()),
        Timer("2", "name 2", persistentListOf()),
    )
    private val timerRepository = mock<ITimerRepository>()

    @Test
    fun `init - sets state`() {
        // given
        every { timerRepository.getTimers() }.returns(timers)
        // when
        viewModel = MainViewModel(timerRepository)
        // then
        assertEquals(2, viewModel.state.value.timers.size)
    }

    @Test
    fun `refresh timers - updates state`() {
        // given
        every { timerRepository.getTimers() }.returns(listOf())
        viewModel = MainViewModel(timerRepository)
        assertEquals(0, viewModel.state.value.timers.size)
        every { timerRepository.getTimers() }.returns(
            listOf(
                Timer(
                    "3",
                    "name 3",
                    persistentListOf()
                )
            )
        )

        // when
        viewModel.refreshData()

        // then
        assertEquals(1, viewModel.state.value.timers.size)
    }

    @Test
    fun `delete timer - deletes timer`() {
        // given
        every { timerRepository.getTimers() }.returns(listOf())
        every { timerRepository.deleteTimer(any()) }.returns(Unit)
        viewModel = MainViewModel(timerRepository)

        // when
        viewModel.deleteTimer(timers[1])

        // then
        verify { timerRepository.deleteTimer(timers[1]) }
    }

    @Test
    fun `duplicate timer - updates state`() {
        // given
        every { timerRepository.getTimers() }.returns(listOf())
        every { timerRepository.duplicate(any()) }.returns(Unit)
        viewModel = MainViewModel(timerRepository)

        // when
        viewModel.duplicateTimer(timers[1])

        // then
        verify { timerRepository.duplicate(timers[1]) }
    }
}
