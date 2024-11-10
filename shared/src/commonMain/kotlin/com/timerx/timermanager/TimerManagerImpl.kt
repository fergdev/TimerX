package com.timerx.timermanager

import com.timerx.coroutines.TxDispatchers
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.timermanager.TimerState.Finished
import com.timerx.timermanager.TimerState.Paused
import com.timerx.timermanager.TimerState.Running
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class TimerManagerImpl(
    private val timerRepository: ITimerRepository,
    private val txDispatchers: TxDispatchers
) : TimerManager {
    private var timerStateMachine: TimerStateMachine? = null

    private val _eventState = MutableStateFlow<TimerEvent>(TimerEvent.Destroy())
    override val eventState: StateFlow<TimerEvent> = _eventState

    override fun startTimer(timer: Timer) {
        require(timerStateMachine == null) {
            "Attempting to start timer while another timer is running"
        }
        initTimerStateMachine(timer)
    }

    private fun initTimerStateMachine(timer: Timer) {
        val childScope = CoroutineScope(txDispatchers.main)
        val timerStateMachine = TimerStateMachineImpl(timer, childScope)
        this.timerStateMachine = timerStateMachine
        childScope.launch {
            timerStateMachine.eventState.collect { timerEvent ->
                _eventState.emit(timerEvent)
                when (timerEvent) {
                    is TimerEvent.Destroy -> {
                        this@TimerManagerImpl.timerStateMachine = null
                        cancel()
                    }

                    is TimerEvent.Finished -> {
                        timerRepository.incrementCompletedCount(timer.id)
                        this@TimerManagerImpl.timerStateMachine = null
                        cancel()
                    }

                    is TimerEvent.Started -> timerRepository.incrementStartedCount(timer.id)
                    else -> {}
                }
            }
        }
    }

    override fun playPause() {
        val timerStateMachine = timerStateMachine
        requireNotNull(timerStateMachine) {
            "Attempting to play/pause with null timer"
        }
        val timerState = timerStateMachine.eventState.value.runState.timerState
        when (timerState) {
            Running -> timerStateMachine.pause()
            Paused -> timerStateMachine.resume()
            Finished -> error("Cannot play/pause finished timer")
        }
    }

    override fun nextInterval() {
        val timerStateMachine = timerStateMachine
        requireNotNull(timerStateMachine) {
            "Attempting to invoke next interval with no timer playing"
        }
        timerStateMachine.nextInterval()
    }

    override fun previousInterval() {
        val timerStateMachine = timerStateMachine
        requireNotNull(timerStateMachine) {
            "Attempting to invoke previous interval with no timer playing"
        }
        timerStateMachine.previousInterval()
    }

    override fun destroy() {
        val timerStateMachine = timerStateMachine
        requireNotNull(timerStateMachine) {
            "Attempting to invoke destroy with no timer playing"
        }
        timerStateMachine.destroy()
        this.timerStateMachine = null
    }

    override fun isRunning() = timerStateMachine != null
}
