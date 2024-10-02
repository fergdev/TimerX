package com.timerx.permissions

import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationPermission

object PermissionManager : IPermissionsHandler {
    override suspend fun requestPermission(permission: Permission) {
        Notification.requestPermission {
            println("On permission result $it")
        }
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState = when {
        Notification.permission == NotificationPermission.Companion.GRANTED -> PermissionState.Granted
        else -> PermissionState.NotGranted
    }

    override fun openAppSettings() {
        // Can't do it on web
    }
}
