package com.timerx.di

import com.timerx.beep.BeepManager
import com.timerx.beep.IBeepManager
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    single<IBeepManager> { BeepManager }
}

