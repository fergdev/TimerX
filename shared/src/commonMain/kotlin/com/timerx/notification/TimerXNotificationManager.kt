package com.timerx.notification

expect class TimerXNotificationManager() {

    fun startService()

    fun stopService()

    fun updateNotification(info: String)
}