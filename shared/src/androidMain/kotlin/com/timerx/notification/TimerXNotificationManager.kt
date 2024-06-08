package com.timerx.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.koin.mp.KoinPlatform

actual class TimerXNotificationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val notificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)!!
    actual fun start() {
        context.startForegroundService(
            Intent(context, NotificationService::class.java)
        )
    }

    actual fun updateNotification(info: String) {
        notificationManager.notify(
            NotificationService.NOTIFICATION_ID,
            createNotification(context, info)
        )
    }

    actual fun stop() {
        context.stopService(
            Intent(context, NotificationService::class.java)
        )
        notificationManager.cancelAll()
    }
}