package com.timerx.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat

class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(this, ""))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "TimerX Notification Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is a silent notification channel"
            setSound(null, null)
        }
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("On destroy")
    }

    companion object {
        const val CHANNEL_ID = "TimerXServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}