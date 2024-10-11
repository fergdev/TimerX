package com.timerx.notification

import co.touchlab.kermit.Logger
import com.timerx.domain.timeFormatted
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Started
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class TimerXNotificationManager : ITimerXNotificationManager {
    override fun start() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val content = UNMutableNotificationContent()
        content.setTitle("Title")
        content.setBody("Body")
        content.setSound(null)

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
        val request =
            UNNotificationRequest.requestWithIdentifier("LocalNotification1", content, trigger)

        center.addNotificationRequest(request) { error ->
            error?.let { Logger.e { "Error: $it" } }
        }
    }

    override fun stop() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllDeliveredNotifications()
    }

    override fun updateNotification(timerEvent: TimerEvent) {
        if (!timerEvent.shouldNotify()) return

        val content = UNMutableNotificationContent().apply {
            setTitle("TimerX - ${timerEvent.runState.timerName}")
            setBody(timerEvent.runState.intervalName)
            setSubtitle(timerEvent.runState.intervalDuration.timeFormatted())
            setSound(null)
        }

        val trigger =
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NOTIFICATION_ID,
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                if (error != null) Logger.e { "Notification error: $error" }
            }
    }

    private fun TimerEvent.shouldNotify() =
        this is NextInterval || this is PreviousInterval ||
            this is Finished || this is Started

    companion object {
        private const val NOTIFICATION_ID = "1"
    }
}
