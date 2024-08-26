package com.timerx.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.timerx.R
import com.timerx.domain.TimerManager
import com.timerx.domain.generateNotificationMessage
import org.koin.mp.KoinPlatform


class NotificationService : Service() {
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val timerManager = KoinPlatform.getKoin().get<TimerManager>()

    private val notificationLayoutSmall by lazy {
        RemoteViews(
            packageName,
            R.layout.custom_notification
        )
    }
    private val notificationLayoutExpanded by lazy {
        RemoteViews(packageName, R.layout.custom_notification_large)
    }

    // activity to service communication
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val orderId = intent.extras?.getLong("WOWO")
                if (orderId != null) {
                    Log.d(
                        "***** SERVICE",
                        "Received intent = ${intent.action} $orderId"
                    )
                    updateProgress(orderId)
                }
            }
        }
    }

    private fun updateProgress(orderId: Long) {
        notificationLayoutSmall.apply {
            setTextViewText(
                R.id.notification_text,
                "Here is an update " + Math.random().toString()
            )
        }
        notificationLayoutExpanded.apply {
            setTextViewText(
                R.id.notification_text,
                "Here is an update large " + Math.random().toString()
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val backgroundColor = timerManager.eventState.value.runState.backgroundColor
        val generateNotificationMessage =
            generateNotificationMessage(timerManager.eventState.value.runState)
        startForeground(
            NOTIFICATION_ID,
            createNotification(this, true, generateNotificationMessage, backgroundColor.toArgb())
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            TIMER_X,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = TIMER_X
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(serviceChannel)
    }

    companion object {
        private const val TIMER_X = "TimerX"
        const val CHANNEL_ID = "TimerXServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}

//class OrderTrackService : Service() {
//
//    @Inject
//    lateinit var api: Api
//
//    companion object {
//        private const val TAG = "OrderTrackService"
//        const val ACTION = "OrderTrackAction"
//        const val CHANNEL_ID = "foreground_service_channel"
//        const val CHANNEL_NAME = "Foreground Service Channel"
//        const val KEY_ORDER_ID = "order_id"
//        const val PROGRESS_DEST_END = 264F
//        const val PROGRESS_DEST_START = 128F
//        const val PROGRESS_REST_START = 0F
//        const val PROGRESS_REST_END = 92F
//    }
//
//    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
//
//    // initialise this later when a rider is assigned
//    private var originalRiderLocation: MapMarker? = null
//
//    // activity to service communication
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            intent?.let {
//                val orderId = intent.extras?.getLong(KEY_ORDER_ID)
//                if (orderId != null) {
//                    Log.d(
//                        TAG,
//                        "Received intent = ${intent.action} $orderId"
//                    )
//                    setAndShowRiderProgress(orderId)
//                }
//            }
//        }
//    }
//
//    private val notificationLayoutSmall by lazy {
//        RemoteViews(
//            packageName,
//            R.layout.zomato_notification_small
//        )
//    }
//    private val notificationLayoutExpanded by lazy {
//        RemoteViews(packageName, R.layout.zomato_notification_expanded)
//    }
//
//    private fun setAndShowRiderProgress(orderId: Long) {
//        // 264 MAX
//        // 0 MIN
//        // 92 ARRIVED WHOLE
//        // 128 ON WAY START
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val orderResp = api.getOrderDetails(orderId)
//                val parsedDetails = parseOrderResponse(orderResp, originalRiderLocation)
//                if (originalRiderLocation == null) {
//                    originalRiderLocation = parsedDetails.riderMapData
//                }
//                notificationLayoutSmall.apply {
//                    setTextViewText(
//                        R.id.zom_notification_title,
//                        "${parsedDetails.restaurantName} • ${parsedDetails.statusDesc}"
//                    )
//                }
//                notificationLayoutExpanded.apply {
//                    if (parsedDetails.status == OrderStatus.CONFIRMED || parsedDetails.status == OrderStatus.IN_KITCHEN_RIDER_NOT_ASSIGNED) {
//                        setImageViewBitmap(R.id.zom_rider, null)
//                    } else {
//                        setImageViewResource(R.id.zom_rider, R.drawable.ic_rider_ev)
//                    }
//                    setTextViewText(R.id.zom_restaurant, parsedDetails.restaurantName)
//                    setTextViewText(R.id.zom_status_title, parsedDetails.statusDesc)
//                    setTextViewText(R.id.zom_status_type, parsedDetails.estimatedTimeDesc)
//                    setTextViewText(R.id.zom_status_time, " | " + parsedDetails.estimatedTime)
//                    setViewLayoutMargin(
//                        R.id.zom_rider,
//                        RemoteViews.MARGIN_START,
//                        parsedDetails.progressPadding,
//                        TypedValue.COMPLEX_UNIT_DIP
//                    )
//                }
//                // re use notification builder
//                createNotification().let {
//                    it.setCustomBigContentView(notificationLayoutExpanded)
//                    notificationManager.notify(
//                        ((orderId.toString().reversed().toLong()) / 1000).toInt(),
//                        it.build()
//                    )
//                }
//                if (parsedDetails.status == OrderStatus.DELIVERED) {
//                    // done
//                } else {
//                    delay(30000) // 30 secs
//                    setAndShowRiderProgress(orderId)
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, "Error occurred while fetching details = $orderId")
//                Log.d(TAG, e.stackTraceToString())
//            }
//        }
//    }
//
//    private fun createNotification(): NotificationCompat.Builder {
//        // Create the NotificationChannel, only for API 26+
//        val notificationChannel =
//            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
//        notificationManager.createNotificationChannel(notificationChannel)
//
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setCustomContentView(notificationLayoutSmall)
//            .setCustomBigContentView(notificationLayoutExpanded)
//            .setContentIntent(pendingIntent)
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        registerReceiver(receiver, IntentFilter(ACTION), RECEIVER_EXPORTED)
//        val notificationChannel =
//            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
//        notificationManager.createNotificationChannel(notificationChannel)
//        val notification =
//            NotificationCompat.Builder(applicationContext, CHANNEL_ID).setContentTitle("Running")
//                .setSmallIcon(R.drawable.ic_checkpoint_restaurant).build()
//        startForeground(1, notification)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        try {
//            unregisterReceiver(receiver)
//        } catch (_: Exception) {
//        }
//    }
//}
//// use first loaded rider pos as min for rider progress
//// ensure that rider pos for otw is always increasing