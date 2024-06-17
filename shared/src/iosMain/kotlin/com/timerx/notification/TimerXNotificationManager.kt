package com.timerx.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual fun getTimerXNotificationManager(): ITimerXNotificationManager = TimerXNotificationManager()

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

    override fun updateNotification(info: String) {
        println("Update notification $info")
        val content = UNMutableNotificationContent().apply {
            setTitle("Title")
            setBody(info)
            setSound(null)
        }

        val trigger =
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(NOTIFICATION_ID, content, trigger)

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                if (error != null) println("Error: $error")
                else println("Success")
            }
    }

    companion object {
        private const val NOTIFICATION_ID = "1"
    }
}