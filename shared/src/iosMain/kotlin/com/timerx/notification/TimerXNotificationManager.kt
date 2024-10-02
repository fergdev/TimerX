package com.timerx.notification

import com.timerx.domain.timeFormatted
import com.timerx.timermanager.TimerEvent
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
            error?.let { println("Error: $it") }
        }
    }

    override fun stop() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllDeliveredNotifications()
    }

    override fun updateNotification(timerEvent: TimerEvent) {
        val content = UNMutableNotificationContent().apply {
            setTitle("TimerX - ${timerEvent.runState.timerName}")
            setBody(timerEvent.runState.intervalName)
            setSubtitle(timerEvent.runState.elapsed.timeFormatted())
            setSound(null)
        }

        val trigger =
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(NOTIFICATION_ID, content, trigger)

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                if (error != null) println("Notification error: $error")
            }
    }

    companion object {
        private const val NOTIFICATION_ID = "1"
    }
}
