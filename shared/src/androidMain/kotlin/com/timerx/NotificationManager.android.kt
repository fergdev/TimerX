package com.timerx

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.timerx.NotificationService.Companion.NOTIFICATION_ID
import org.koin.mp.KoinPlatform.getKoin

actual class TimerXNotificationManager {
    private val context: Context = getKoin().get()
    actual fun startService() {
        // TODO remove this sdk check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(
                Intent(context, NotificationService::class.java)
            )
        }
    }

    actual fun updateNotification(info: String) {
        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager!!.notify(NOTIFICATION_ID, createNotification(info))
    }

    actual fun stopService() {
        context.stopService(
            Intent(context, NotificationService::class.java)
        )
    }

    private fun createNotification(info: String): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setContentTitle("TimerX Service")
            .setContentText(info)
            .setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSound(null)
            .build()
    }
}