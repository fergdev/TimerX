package com.timerx.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import org.koin.mp.KoinPlatform

actual fun getTimerXNotificationManager(): ITimerXNotificationManager = TimerXNotificationManager()

class TimerXNotificationManager : ITimerXNotificationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    override fun start() {
        context.startForegroundService(Intent(context, NotificationService::class.java))
    }

    override fun updateNotification(isRunning: Boolean, info: String, backgroundColor: Int) {
        notificationManager.notify(
            NotificationService.NOTIFICATION_ID,
            createNotification(context, isRunning, info, backgroundColor)
        )
    }

    override fun stop() {
        context.stopService(Intent(context, NotificationService::class.java))
        notificationManager.cancelAll()
    }
}
