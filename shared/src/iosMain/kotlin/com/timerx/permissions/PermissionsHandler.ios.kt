package com.timerx.permissions

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual fun permissionsHandler(): IPermissionsHandler {
    return PermissionsHandler()
}

class PermissionsHandler : IPermissionsHandler {
    private fun getDelegate(permission: Permission): PermissionDelegate {
        return when (permission) {
            Permission.Notification -> {
                RemoteNotificationPermissionDelegate()
            }
        }
    }

    override suspend fun requestPermission(permission: Permission) {
        getDelegate(permission).requestPermission()
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return getDelegate(permission).getPermissionState()
    }

    override fun openAppSettings() {
        doOpenAppSettings()
    }
}

internal fun doOpenAppSettings() {
    val settingsUrl: NSURL = NSURL.URLWithString(UIApplicationOpenSettingsURLString)!!
    UIApplication.sharedApplication.openURL(settingsUrl)
}
