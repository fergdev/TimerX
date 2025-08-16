package com.intervallum.permissions

import com.intervallum.permissions.PermissionState.Granted
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PermissionsHandlerTest : FreeSpec({

    val factory: () -> PermissionsHandler = { PermissionsHandler() }

    "default" {
        val permissionHandler = factory()
        // noop
        permissionHandler.requestPermission(Permission.Notification)
        permissionHandler.getPermissionState(Permission.Notification) shouldBe Granted
        // noop
        permissionHandler.openAppSettings()
    }
})
