package com.timerx.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.timerx.MainActivity
import com.timerx.R
import com.timerx.ui.common.contrastColor

const val NOTIFICATION_PLAY = "play"
const val NOTIFICATION_STOP = "stop"
const val NOTIFICATION_KEY = "key"

fun createNotification(
    context: Context,
    isRunning: Boolean,
    info: String,
    backgroundColor: Int
): Notification {
    val contrastColor = Color(backgroundColor).contrastColor().toArgb()
    val appPendingIntent = appPendingIntent(context)
    val playPausePendingIntent = playPausePendingIntent(context)
    val stopPendingIntent = destroyPendingIntent(context)

    val playPauseIcon = getTintedBitmap(
        context,
        if (isRunning) R.drawable.pause else R.drawable.play_arrow,
        contrastColor
    )
    val cancelIcon = getTintedBitmap(
        context,
        R.drawable.close,
        contrastColor
    )
    val customLayout = RemoteViews(context.packageName, R.layout.custom_notification).apply {

        setTextViewText(R.id.notification_text, info)
        setTextColor(R.id.notification_text, contrastColor)
        setImageViewIcon(R.id.notification_pause, playPauseIcon)
        setImageViewIcon(R.id.notification_cancel, cancelIcon)
//        setImageViewResource(
//            R.id.notification_pause,
//        )
        setOnClickPendingIntent(R.id.notification_pause, playPausePendingIntent)
        setOnClickPendingIntent(R.id.notification_cancel, stopPendingIntent)
    }
//    val notificationLayoutExpanded =
//        RemoteViews(context.packageName, R.layout.custom_notification_large).apply {
//            setTextViewText(R.id.notification_title, "TimerX running")
//            setTextViewText(R.id.notification_text, info)
//        }
//    val headsUpRemoteView =
//        RemoteViews(context.packageName, R.layout.custom_notification_large).apply {
//            setTextViewText(R.id.notification_title, "TimerX running")
//            setTextViewText(R.id.notification_text, info)
//        }
//
    return NotificationCompat.Builder(context, NotificationService.CHANNEL_ID).apply {
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setCustomContentView(customLayout)
//        setCustomHeadsUpContentView(headsUpRemoteView)
//        setCustomBigContentView(customLayout)
        setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
        setColor(backgroundColor)
        setColorized(true)
        setStyle(NotificationCompat.DecoratedCustomViewStyle())


        setContentIntent(appPendingIntent)
        setOnlyAlertOnce(true)
        setOngoing(true)
        setSound(Uri.EMPTY)
        setAutoCancel(false)
    }.build()
}

private fun destroyPendingIntent(context: Context): PendingIntent {
    val stopIntent = Intent(context, MainActivity::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_STOP)
    }
    val stopPendingIntent = PendingIntent.getActivity(
        context,
        2,
        stopIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        Bundle().apply {
            putBoolean(NOTIFICATION_STOP, true)
        }
    )
    return stopPendingIntent
}

private fun playPausePendingIntent(context: Context): PendingIntent {
    val playPauseIntent = Intent(context, MainActivity::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_PLAY)
    }
    val playPausePendingIntent = PendingIntent.getActivity(
        context,
        1,
        playPauseIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
    )
    return playPausePendingIntent
}

private fun appPendingIntent(context: Context): PendingIntent {
    val appIntent = Intent(context, MainActivity::class.java)
    val appPendingIntent = PendingIntent.getActivity(
        context,
        0,
        appIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    return appPendingIntent
}

private fun getTintedBitmap(context: Context, drawableId: Int, color: Int): Icon {
    val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    val icon = Icon.createWithBitmap(bitmap)
    icon.setTint(color)
    return icon
}