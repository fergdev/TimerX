package com.timerx.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.mp.KoinPlatform
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

private data class PermissionCallback(
    val permission: Permission,
    val callback: (Result<Unit>) -> Unit
)

class PermissionsHandler : IPermissionsHandler {
    val activity: ComponentActivity by lazy { KoinPlatform.getKoin().get() }
    private val mutex: Mutex = Mutex()
    private val key = UUID.randomUUID().toString()
    private var permissionCallback: PermissionCallback? = null

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        if (permission == Permission.Notification &&
            Build.VERSION.SDK_INT in VERSIONS_WITHOUT_NOTIFICATION_PERMISSION
        ) {
            val isNotificationsEnabled = NotificationManagerCompat.from(activity)
                .areNotificationsEnabled()
            return if (isNotificationsEnabled) {
                PermissionState.Granted
            } else {
                PermissionState.DeniedAlways
            }
        }

        val permissions: List<String> = permission.toPlatformPermission()
        val status: List<Int> = permissions.map {
            ContextCompat.checkSelfPermission(activity, it)
        }
        val isAllGranted: Boolean = status.all { it == PackageManager.PERMISSION_GRANTED }
        if (isAllGranted) return PermissionState.Granted

        val isAllRequestRationale: Boolean = permissions.all {
            shouldShowRequestPermissionRationale(activity, it).not()
        }
        return if (isAllRequestRationale) PermissionState.Denied
        else PermissionState.NotGranted
    }

    private fun Permission.toPlatformPermission() =
        when (this) {
            Permission.Notification -> remoteNotificationsPermissions()
        }

    override fun openAppSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", activity.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
    }

    override suspend fun requestPermission(permission: Permission) {
        mutex.withLock {
            val launcher = bind(activity)
            val platformPermission = permission.toPlatformPermission()
            suspendCoroutine { continuation ->
                requestPermission(
                    launcher,
                    permission,
                    platformPermission
                ) { continuation.resumeWith(it) }
            }
        }
    }

    private fun requestPermission(
        launcher: ActivityResultLauncher<Array<String>>,
        permission: Permission,
        permissions: List<String>,
        callback: (Result<Unit>) -> Unit
    ) {
        permissionCallback = PermissionCallback(permission, callback)
        launcher.launch(permissions.toTypedArray())
    }

    private fun bind(activity: ComponentActivity): ActivityResultLauncher<Array<String>> {
        val activityResultRegistryOwner = activity as ActivityResultRegistryOwner

        return activityResultRegistryOwner.activityResultRegistry.register(
            key,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isCancelled = permissions.isEmpty()

            val permissionCallback = permissionCallback ?: return@register

            if (isCancelled) {
                permissionCallback.callback.invoke(
                    Result.failure(RequestCanceledException(permissionCallback.permission))
                )
                return@register
            }

            val success = permissions.values.all { it }

            if (success) {
                permissionCallback.callback.invoke(Result.success(Unit))
            } else {
                if (shouldShowRequestPermissionRationale(activity, permissions.keys.first())) {
                    permissionCallback.callback.invoke(
                        Result.failure(DeniedException(permissionCallback.permission))
                    )
                } else {
                    permissionCallback.callback.invoke(
                        Result.failure(DeniedAlwaysException(permissionCallback.permission))
                    )
                }
            }
        }
    }

    private fun remoteNotificationsPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }

    companion object {
        private val VERSIONS_WITHOUT_NOTIFICATION_PERMISSION =
            Build.VERSION_CODES.KITKAT until Build.VERSION_CODES.TIRAMISU
    }
}
