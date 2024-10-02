package com.timerx.permissions

class PermissionsHandler : IPermissionsHandler {
    override suspend fun requestPermission(permission: Permission) {
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return PermissionState.Granted
    }

    override fun openAppSettings() {
    }
}
