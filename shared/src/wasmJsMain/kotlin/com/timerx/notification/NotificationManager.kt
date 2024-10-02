package com.timerx.notification

import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.Started
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_name

object NotificationManager : ITimerXNotificationManager {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun start() {
    }

    override fun stop() {
    }

    override fun updateNotification(timerEvent: TimerEvent) {
        coroutineScope.launch {
            when (timerEvent) {
                is Started, is NextInterval, is Finished -> {
                    Notification(
                        title = getString(Res.string.app_name),
                        options = NotificationOptions(
                            body = timerEvent.runState.intervalName,
                            // TODO set icon
                        )
                    )
                }

                else -> {
                    // ignore
                }
            }
        }
    }
}