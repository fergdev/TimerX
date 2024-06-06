package com.timerx

expect class TimerXNotificationManager() {

    fun startService()

    fun stopService()

    fun updateNotification(info: String)
}