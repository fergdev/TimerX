package com.timerx.notification

import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.Started
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_name

internal class NotificationManager(timerManager: TimerManager) {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        coroutineScope.launch {
            timerManager.eventState.collect {
                when (it) {
                    is Started, is NextInterval, is Finished -> {
                        Notification(
                            title = getString(Res.string.app_name),
                            options = NotificationOptions(
                                body = it.runState.intervalName,
                            )
                        )
                    }
                    else -> { /* ignore */ }
                }
            }
        }
    }
}
