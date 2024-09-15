package com.timerx.di

import com.timerx.database.ITimerRepository
import com.timerx.database.InMemoryTimerXRepository
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.InMemoryTimerXSettings
import org.koin.dsl.module

actual val platformModule = module {
    single<ITimerXSettings> { InMemoryTimerXSettings }
    single<ITimerRepository> { InMemoryTimerXRepository }
}

