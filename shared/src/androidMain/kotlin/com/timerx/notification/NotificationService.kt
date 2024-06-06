package com.timerx.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.timerx.MainActivity

class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "TimerX Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)

        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TimerX Service")
            .setContentText("Foreground Service is running...")
            .setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSound(null)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}