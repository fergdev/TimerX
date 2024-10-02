package com.timerx.di

import com.timerx.beep.ABeepManager
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.KStoreDatabase
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionManager
import com.timerx.platform.wasmCapabilities
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    single<ITimerRepository> { KStoreDatabase() }
    single<IVibrationManager> { VibrationManager() }
    single { wasmCapabilities }
    singleOf(::ABeepManager) { bind<IBeepManager>() }
    single<ITimerXNotificationManager> { NotificationManager }
    single<IPermissionsHandler> { PermissionManager }
}
