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
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.timerx.MainActivity
import com.timerx.R
import com.timerx.domain.timeFormatted
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerState
import com.timerx.ui.common.contrastColor

const val NOTIFICATION_APP_REQUEST_CODE = 0
const val NOTIFICATION_PLAY_PAUSE = "play_pause"
const val NOTIFICATION_PLAY_PAUSE_ID = 1
const val NOTIFICATION_SKIP_PREVIOUS = "skip_previous"
const val NOTIFICATION_SKIP_PREVIOUS_ID = 2
const val NOTIFICATION_SKIP_NEXT = "skip_next"
const val NOTIFICATION_SKIP_NEXT_ID = 3
const val NOTIFICATION_STOP = "stop"
const val NOTIFICATION_STOP_ID = 4
const val NOTIFICATION_KEY = "notification_key"

@RequiresApi(Build.VERSION_CODES.Q)
fun createNotification(
    context: Context,
    timerEvent: TimerEvent,
): Notification {
    val backgroundColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        timerEvent.runState.backgroundColor.toArgb()
    } else {
        Color.White.toArgb()
    }
    val isRunning = timerEvent.runState.timerState == TimerState.Running
    val contrastColor = Color(backgroundColor).contrastColor().toArgb()
    val time = (timerEvent.runState.intervalDuration - timerEvent.runState.elapsed).timeFormatted()

    val appPendingIntent = appPendingIntent(context)
    val playPausePendingIntent = playPausePendingIntent(context)
    val stopPendingIntent = destroyPendingIntent(context)

    val skipPreviousPendingIntent = skipPreviousPendingIntent(context)
    val skipNextPendingIntent = skipNextPendingIntent(context)

    val customLayout = getSmallView(
        context,
        timerEvent,
        contrastColor,
        time,
        isRunning,
        playPausePendingIntent,
        stopPendingIntent
    )

    val large = getLargeView(
        context,
        timerEvent,
        contrastColor,
        time,
        isRunning,
        playPausePendingIntent,
        stopPendingIntent,
        skipNextPendingIntent,
        skipPreviousPendingIntent
    )

    return NotificationCompat.Builder(context, NotificationService.CHANNEL_ID).apply {
        setAutoCancel(false)
        setCategory(NotificationCompat.CATEGORY_WORKOUT)
        setColor(backgroundColor)
        setColorized(true)
        setContentIntent(appPendingIntent)

        setCustomContentView(customLayout)
        setCustomBigContentView(large)

        setOngoing(true)
        setOnlyAlertOnce(true)
        setSilent(true)
        setSmallIcon(R.drawable.av_timer)
        setPriority(NotificationCompat.PRIORITY_MAX)

        setShowWhen(false)

        setSound(Uri.EMPTY)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }.build()
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getSmallView(
    context: Context,
    timerEvent: TimerEvent,
    contrastColor: Int,
    time: String,
    isRunning: Boolean,
    playPausePendingIntent: PendingIntent,
    stopPendingIntent: PendingIntent
): RemoteViews {
    val customLayout = RemoteViews(context.packageName, R.layout.custom_notification).apply {
        setTextViewText(R.id.notification_interval, timerEvent.runState.intervalName)
        setTextColor(R.id.notification_interval, contrastColor)

        setTextViewText(R.id.notification_time, time)
        setTextColor(R.id.notification_time, contrastColor)

        setImageViewIcon(
            R.id.notification_pause,
            getTintedIcon(
                context,
                if (isRunning) R.drawable.pause else R.drawable.play_arrow,
                contrastColor
            )
        )
        setImageViewIcon(
            R.id.notification_cancel,
            getTintedIcon(
                context,
                R.drawable.close,
                contrastColor
            )
        )
        setOnClickPendingIntent(R.id.notification_pause, playPausePendingIntent)
        setOnClickPendingIntent(R.id.notification_cancel, stopPendingIntent)
    }
    return customLayout
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getLargeView(
    context: Context,
    timerEvent: TimerEvent,
    contrastColor: Int,
    time: String,
    isRunning: Boolean,
    playPausePendingIntent: PendingIntent,
    stopPendingIntent: PendingIntent,
    skipNextPendingIntent: PendingIntent,
    skipPreviousPendingIntent: PendingIntent
): RemoteViews {
    val large = RemoteViews(context.packageName, R.layout.custom_notification_large).apply {
        setTextViewText(R.id.notification_interval, timerEvent.runState.intervalName)
        setTextColor(R.id.notification_interval, contrastColor)

        setTextViewText(R.id.notification_time, time)
        setTextColor(R.id.notification_time, contrastColor)

        setImageViewIcon(
            R.id.notification_pause,
            getTintedIcon(
                context,
                if (isRunning) R.drawable.pause else R.drawable.play_arrow,
                contrastColor
            )
        )
        setImageViewIcon(
            R.id.notification_cancel,
            getTintedIcon(
                context,
                R.drawable.close,
                contrastColor
            )
        )
        setImageViewIcon(
            R.id.notification_skip_next,
            getTintedIcon(
                context,
                R.drawable.skip_next,
                contrastColor
            )
        )
        setImageViewIcon(
            R.id.notification_skip_previous,
            getTintedIcon(
                context,
                R.drawable.skip_previous,
                contrastColor
            )
        )
        setOnClickPendingIntent(R.id.notification_pause, playPausePendingIntent)
        setOnClickPendingIntent(R.id.notification_cancel, stopPendingIntent)
        setOnClickPendingIntent(R.id.notification_skip_next, skipNextPendingIntent)
        setOnClickPendingIntent(R.id.notification_skip_previous, skipPreviousPendingIntent)
    }
    return large
}

private fun destroyPendingIntent(context: Context): PendingIntent {
    val stopIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_STOP)
    }
    val stopPendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_STOP_ID,
        stopIntent,
        PendingIntent.FLAG_MUTABLE,
    )
    return stopPendingIntent
}

private fun playPausePendingIntent(context: Context): PendingIntent {
    val playPauseIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_PLAY_PAUSE)
    }
    val playPausePendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_PLAY_PAUSE_ID,
        playPauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    return playPausePendingIntent
}

private fun skipPreviousPendingIntent(context: Context): PendingIntent {
    val skipPreviousIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_SKIP_PREVIOUS)
    }
    val skipPreviousPendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_SKIP_PREVIOUS_ID,
        skipPreviousIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    return skipPreviousPendingIntent
}

private fun skipNextPendingIntent(context: Context): PendingIntent {
    val skipNextIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NOTIFICATION_KEY, NOTIFICATION_SKIP_NEXT)
    }
    val skipNextPausePendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_SKIP_NEXT_ID,
        skipNextIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    return skipNextPausePendingIntent
}

private fun appPendingIntent(context: Context): PendingIntent {
    val appIntent = Intent(context, MainActivity::class.java)
    val appPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_APP_REQUEST_CODE,
        appIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    return appPendingIntent
}

private fun getTintedIcon(context: Context, drawableId: Int, color: Int): Icon {
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
