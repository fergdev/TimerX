package com.timerx.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.timerx.MainActivity
import com.timerx.R

fun createNotification(context: Context, info: String): Notification {
    val notificationIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    val customLayout = RemoteViews(context.packageName, R.layout.custom_notification).apply {
        setTextViewText(R.id.notification_title, "TimerX running")
        setTextViewText(R.id.notification_text, info)
    }
    val notificationLayoutExpanded = RemoteViews(context.packageName, R.layout.custom_notification_large).apply {
        setTextViewText(R.id.notification_title, "TimerX running")
        setTextViewText(R.id.notification_text, info)
    }
    val headsUpRemoteView = RemoteViews(context.packageName, R.layout.custom_notification_large).apply {
        setTextViewText(R.id.notification_title, "TimerX running")
        setTextViewText(R.id.notification_text, info)
    }

    return NotificationCompat.Builder(context, NotificationService.CHANNEL_ID).apply {
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setCustomContentView(customLayout)
        setCustomBigContentView(notificationLayoutExpanded)
        setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
        setContentIntent(pendingIntent)
        setOnlyAlertOnce(true)
        setOngoing(true)
        setSound(Uri.EMPTY)
        setCustomHeadsUpContentView(headsUpRemoteView)
    }.build()
}
