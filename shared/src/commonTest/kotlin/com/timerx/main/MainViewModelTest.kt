@file:OptIn(ExperimentalCoroutinesApi::class)

package com.timerx.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.ui.main.MainViewModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private val timers = mutableListOf(
        Timer(id = 1L, name = "name 1", sets = persistentListOf()),
        Timer(id = 2L, name = "name 2", sets = persistentListOf()),
    )
    private val timerRepository = mock<ITimerRepository>()
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - sets state`() = runTest {
        // given
        everySuspend { timerRepository.getTimers() }.returns(timers)
        // when
        viewModel = MainViewModel(timerRepository)
        // then
        assertEquals(2, viewModel.state.value.timers.size)
    }

    @Test
    fun `refresh timers - updates state`() {
        // given
        everySuspend { timerRepository.getTimers() }.returns(listOf())
        viewModel = MainViewModel(timerRepository)
        assertEquals(0, viewModel.state.value.timers.size)
        everySuspend { timerRepository.getTimers() }.returns(
            listOf(
                Timer(
                    id = 3L,
                    name = "name 3",
                    sets = persistentListOf()
                )
            )
        )

        // when
        viewModel.interactions.refreshData()

        // then
        assertEquals(1, viewModel.state.value.timers.size)
    }

    @Test
    fun `delete timer - deletes timer`() {
        // given
        everySuspend { timerRepository.getTimers() }.returns(listOf())
        everySuspend { timerRepository.deleteTimer(any()) }.returns(Unit)
        viewModel = MainViewModel(timerRepository)

        // when
        viewModel.interactions.deleteTimer(timers[1])

        // then
        verifySuspend { timerRepository.deleteTimer(timers[1]) }
    }

    @Test
    fun `duplicate timer - updates state`() {
        // given
        everySuspend { timerRepository.getTimers() }.returns(listOf())
        everySuspend { timerRepository.duplicate(any()) }.returns(Unit)
        viewModel = MainViewModel(timerRepository)

        // when
        viewModel.interactions.duplicateTimer(timers[1])

        // then
        verifySuspend { timerRepository.duplicate(timers[1]) }
    }

    @Test
    fun `swap timers - swaps timers`() {
        // given
        everySuspend { timerRepository.getTimers() }.returns(timers)
        everySuspend { timerRepository.swapTimers(any(), any()) }.returns(Unit)
        viewModel = MainViewModel(timerRepository)

        // when
        viewModel.interactions.swapTimers(0, 1)

        // then
        assertEquals(viewModel.state.value.timers, listOf(timers[1], timers[0]))
    }
}
