package com.intervallum.permissions

internal interface PermissionDelegate {
    suspend fun requestPermission()
    suspend fun getPermissionState(): PermissionState
}
