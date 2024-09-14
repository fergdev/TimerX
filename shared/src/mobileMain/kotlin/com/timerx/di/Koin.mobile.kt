package com.timerx.di

import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepository
import com.timerx.database.createRoomDatabaseFactory
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.TimerXSettings
import com.timerx.settings.dataStorePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    single { dataStorePreferences(coroutineScope = CoroutineScope(Dispatchers.IO)) }
    singleOf(::TimerXSettings) { bind<ITimerXSettings>() }
    single { createRoomDatabaseFactory().createRoomDataBase() }
    singleOf(::TimerRepository) { bind<ITimerRepository>() }
}
