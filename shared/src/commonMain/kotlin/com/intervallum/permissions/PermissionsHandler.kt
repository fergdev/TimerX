package com.intervallum.permissions

// all inspired from https://github.com/icerockdev/moko-permissions/blob/master/permissions/src/commonMain/kotlin/dev/icerock/moko/permissions/PermissionsController.kt

interface IPermissionsHandler {
    suspend fun requestPermission(permission: Permission)
    suspend fun getPermissionState(permission: Permission): PermissionState
    fun openAppSettings()
}

enum class Permission {
    Notification
}

enum class PermissionState {

    /**
     * Starting state for each permission.
     */
    NotDetermined,

    /**
     * Android-only. This could mean [NotDetermined] or [DeniedAlways], but the OS doesn't
     * expose which of the two it is in all scenarios.
     */
    NotGranted,

    Granted,

    /**
     * Android-only.
     */
    Denied,

    /**
     * On Android only applicable to Push Notifications.
     */
    DeniedAlways
}

class RequestCanceledException(
    val permission: Permission,
    message: String? = null
) : Exception(message)

open class DeniedException(
    val permission: Permission,
    message: String? = null
) : Exception(message)

class DeniedAlwaysException(
    permission: Permission,
    message: String? = null
) : DeniedException(permission, message)
