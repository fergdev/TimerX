package com.timerx.di

import com.timerx.beep.BeepManager
import com.timerx.beep.IBeepManager
import com.timerx.capabilities.desktopCapabilities
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    includes(mobileModule)
    single{ desktopCapabilities }
    single<IBeepManager> { BeepManager }
}

