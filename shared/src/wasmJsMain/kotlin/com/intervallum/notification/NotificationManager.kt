package com.intervallum.notification

import com.intervallum.timermanager.TimerEvent.Finished
import com.intervallum.timermanager.TimerEvent.NextInterval
import com.intervallum.timermanager.TimerEvent.Started
import com.intervallum.timermanager.TimerManager
import intervallum.shared.generated.resources.Res
import intervallum.shared.generated.resources.app_name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions

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
