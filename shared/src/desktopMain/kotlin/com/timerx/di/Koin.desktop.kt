package com.timerx.di

import com.timerx.beep.BeepManager
import com.timerx.beep.IBeepManager
import com.timerx.capabilities.desktopCapabilities
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionHandler
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.NoopVibrationManager
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    includes(mobileModule)
    single { desktopCapabilities }
    single<ITimerXNotificationManager> { NotificationManager }
    single<IVibrationManager> { NoopVibrationManager }
    single<IBeepManager> { BeepManager }
    single<IPermissionsHandler> { PermissionHandler }
}
