@file:Suppress("Filename")
package com.timerx.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.timerx.timermanager.TimerEvent
import org.koin.mp.KoinPlatform

class NotificationManager : ITimerXNotificationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val androidNotificationManager by lazy {
        context.getSystemService(NotificationManager::class.java)
    }

    override fun start() {
        context.startForegroundService(Intent(context, NotificationService::class.java))
    }

    override fun updateNotification(
        timerEvent: TimerEvent
    ) {
        androidNotificationManager.notify(
            NotificationService.NOTIFICATION_ID,
            createNotification(context, timerEvent)
        )
    }

    override fun stop() {
        context.stopService(Intent(context, NotificationService::class.java))
        androidNotificationManager.cancelAll()
    }
}
