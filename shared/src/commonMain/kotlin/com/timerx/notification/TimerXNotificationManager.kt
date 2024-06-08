package com.timerx.notification

expect class TimerXNotificationManager() {

    fun start()

    fun stop()

    fun updateNotification(info: String)
}