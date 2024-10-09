package com.timerx.permissions

class PermissionsHandler : IPermissionsHandler {
    override suspend fun requestPermission(permission: Permission) {
        // Noop
    }

    override suspend fun getPermissionState(permission: Permission) = PermissionState.Granted

    override fun openAppSettings() {
        // Noop
    }
}
