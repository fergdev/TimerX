package com.timerx.permissions

actual fun permissionsHandler()=object :IPermissionsHandler {
    override suspend fun requestPermission(permission: Permission) {
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return PermissionState.Granted
    }

    override fun openAppSettings() {
    }
}