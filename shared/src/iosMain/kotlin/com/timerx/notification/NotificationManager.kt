package com.timerx.notification

import co.touchlab.kermit.Logger
import com.timerx.domain.timeFormatted
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Started
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger.Companion.triggerWithTimeInterval
import platform.UserNotifications.UNUserNotificationCenter

class NotificationManager(private val timerManager: TimerManager) {
    private val center = UNUserNotificationCenter.currentNotificationCenter()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        coroutineScope.launch {
            timerManager.eventState.collect { timerEvent ->
                updateNotification(timerEvent)
            }
        }
    }

    private fun updateNotification(timerEvent: TimerEvent) {
        if (!timerEvent.shouldNotify()) return

        val content = UNMutableNotificationContent().apply {
            setTitle("TimerX - ${timerEvent.runState.timerName}")
            setBody(timerEvent.runState.intervalName)
            setSubtitle(timerEvent.runState.intervalDuration.timeFormatted())
            setSound(null)
        }

        val trigger = triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NOTIFICATION_ID,
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request) { error ->
            error?.let { Logger.e { "Notification error: $it" } }
        }
    }

    private fun TimerEvent.shouldNotify() =
        this is NextInterval || this is PreviousInterval ||
            this is Finished || this is Started

    companion object {
        private const val NOTIFICATION_ID = "1"
    }
}
